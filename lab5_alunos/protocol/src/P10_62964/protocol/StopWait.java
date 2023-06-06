/*
 * Sistemas de Telecomunicacoes 
 *          2022/2023
 */
package protocol;

import simulator.AckFrameIF;
import simulator.DataFrameIF;
import terminal.Simulator;
import simulator.Frame;
import terminal.NetworkLayer;
import terminal.Terminal;

/**
 * Protocol 3 : Stop & Wait protocol
 * 
 * @author Martim Agostinho 62964
 */
public class StopWait extends Base_Protocol implements Callbacks {

    public StopWait(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol
        next_frame_to_send = 0;        
        frame_expected = 0;

        // Initialize here all variables
        // ...
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nStop&Wait Protocol\n\n");
        send_next_data_packet();    // Start sending the first packet
    }

    /**
     * CALLBACK FUNCTION: handle the end of Data frame transmission, start timer
     * @param time current simulation time
     * @param seq  sequence number of the Data frame transmitted
     */
    @Override
    public void handle_Data_end(long time, int seq) {
        sim.start_data_timer(seq);
    }
    
    /**
     * CALLBACK FUNCTION: handle the timer event; retransmit failed frames
     * @param time current simulation time
     * @param key  timer key (sequence number)
     */
    @Override
    public void handle_Data_Timer(long time, int key) {
        sim.Log("Resending packet\n");
        if( sim.is_sending_data() ){ 
            return;
        }
        send_packet(CurrentPacket);

    }
    
    /**
     * CALLBACK FUNCTION: handle the ack timer event; send ACK frame
     * @param time current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {
        sim.Log("ACK timer ended. Sending ACK\n");
        
        if( sim.is_sending_data() ){ return; }
        
        Frame ack_frame = Frame.new_Ack_Frame(last_DataF_rcv.seq(), last_DataF_rcv.rcvbufsize());
        sim.to_physical_layer(ack_frame, false /* do not interrupt an ongoing transmission*/);
        
    }

    /**
     * CALLBACK FUNCTION: handle the reception of a frame from the physical layer
     * @param time current simulation time
     * @param frame frame received
     */
    @Override
    public void from_physical_layer(long time, Frame frame) {
        
        sim.Log("Recieving frames...\n");
        
        if(frame.kind() == Frame.DATA_FRAME){
            sim.Log("Data Frame\n");
            last_DataF_rcv = frame;  // Auxiliary variable to access the Data frame fields.
            
            if (last_DataF_rcv.seq() == frame_expected) {    // Check the sequence number
                // Send the frame to the network layer
                if (net.to_network_layer(last_DataF_rcv.info())){
                    frame_expected = next_seq(frame_expected);
                    sim.start_ack_timer();
                }//else{ sim.Log("?????????\n"); }
                
            }else{ 
                
                if( !sim.is_sending_data() ){
                    Frame ack_frame = Frame.new_Ack_Frame( prev_seq( frame_expected ), last_DataF_rcv.rcvbufsize());
                    sim.to_physical_layer(ack_frame, false /* do not interrupt an ongoing transmission*/);
                }        //frame_expected = frame.seq();
                sim.Log("Error frame seq not expected\n"
                        + "Expected seq: "+frame_expected+"\n"
                                + "Frame recieved:"+last_DataF_rcv.seq()+"\n");
            }
            
            if( last_DataF_rcv.ack() == next_frame_to_send ){
                sim.Log("With piggy backing\n");
                handle_ack(last_DataF_rcv.ack());
                
            }
        }else if(frame.kind() == Frame.ACK_FRAME){
            sim.Log("Ack Frame\n");
            AckFrameIF aframe = frame;
            
            // next_frame_to_send is only updated after the handle_ack()
            if( aframe.ack() !=  next_frame_to_send  ){ 
                sim.Log(next_frame_to_send+" Next frame to send\n");//DEBUG
                return;
            }
            
            handle_ack(aframe.ack());
            
        }else{ sim.Log("Error: not ack or Data\n"); }
        
    }

    /**
     * CALLBACK FUNCTION: handle the end of the simulation
     * @param time current simulation time
     */
    @Override
    public void end_simulation(long time) {
        sim.Log("Stopping simulation\n");
    }
    
    private void handle_ack(int ack){
    
        if( !sim.is_sending_data() ){
            sim.cancel_data_timer( next_frame_to_send );
            next_frame_to_send = next_seq(next_frame_to_send);
            send_next_data_packet();
        }
    }
    
    private void send_next_data_packet() {
                
        CurrentPacket = net.from_network_layer(); //Pacote
        if (CurrentPacket != null /*Significa que nao ha mais*/ ) {
            send_packet(CurrentPacket);
        }
    }
    
    private void send_packet(String packet) {
        //Criar frame
        int ack  =  last_DataF_rcv == null ? prev_seq(0) : last_DataF_rcv.seq();//sim.isactive_ack_timer() ? last_DataF_rcv.seq() : prev_seq(0); //saber se ha ou nao piggybacking
        
        Frame frame = Frame.new_Data_Frame(next_frame_to_send /*seq*/, 
                    ack/* ack= the one before 0 */, 
                    net.get_recvbuffsize() /* returns the buffer space available in the network layer */,
                    packet);
        sim.to_physical_layer(frame, false /* do not interrupt an ongoing transmission*/);
    }
    
    /* Variables */
    
    private int next_frame_to_send;      //to get 
    private DataFrameIF last_DataF_rcv; //
    private String CurrentPacket;
    private int frame_expected;

    /**
     * Reference to the simulator (Terminal), to get the configuration and send commands
     */
    //final Simulator sim;  -  Inherited from Base_Protocol
    
    /**
     * Reference to the network layer, to send a receive packets
     */
    //final NetworkLayer net;    -  Inherited from Base_Protocol
}