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
 * @author 62964 Martim Agostinho
 */
public class StopWait extends Base_Protocol implements Callbacks {

    public StopWait(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol
        frame_expected = 0;
        next_frame_to_send = 0;        
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nStop&Wait Protocol\n\n");
        send_next_data_packet();    // Start sending the first packet

        //sim.Log("\nNot implemented yet\n\n");    
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
        sim.Log("handle_Data_Timer Seq"+ key +":\n"+"Packet: "+CurrentPacket+"\n");        
        send_packet(CurrentPacket);    
    }
    
    /**
     * CALLBACK FUNCTION: handle the ack timer event; send ACK frame
     * @param time current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {
        sim.Log("ACK Timer ended sending ACK\n");
         Frame ack_frame = Frame.new_Ack_Frame(ACK_frame.seq(), ACK_frame.rcvbufsize());
            sim.to_physical_layer(ack_frame, false /* do not interrupt an ongoing transmission*/);
            
            if (ACK_frame.seq() != prev_seq(frame_expected) ) {    // Check the sequence number
                // Send the frame to the network layer
                //frame_expected = frame.seq();
                sim.Log("Error frame seq not expected\n"
                        + "Expected seq: "+frame_expected+"\n"
                                + "Frame recieved:"+ACK_frame.seq()+"\n");
            }
    }

    /**
     * CALLBACK FUNCTION: handle the reception of a frame from the physical layer
     * @param time current simulation time
     * @param frame frame received
     */
    @Override
    public void from_physical_layer(long time, Frame frame) {
        sim.Log("from_physical_layer \n");
        if (frame.kind() == Frame.DATA_FRAME) {     // Check the frame kind
            DataFrameIF dframe= frame;  // Auxiliary variable to access the Data frame fields.

            if (!sim.isactive_ack_timer() ){//ativar o timer do ACK
                sim.start_ack_timer();
                ACK_frame = dframe;
            }
            if (dframe.seq() == frame_expected) {    // Check the sequence number
                // Send the frame to the network layer
                if (net.to_network_layer(dframe.info())){
                   // prev_frame = frame_expected;
                    frame_expected = next_seq(frame_expected);
                }
            }else{ 
                //frame_expected = frame.seq();
                sim.Log("Error frame seq not expected\n"
                        + "Expected seq: "+frame_expected+"\n"
                                + "Frame recieved:"+dframe.seq()+"\n");
            }
            
        }
        else if(frame.kind() == Frame.ACK_FRAME){
            //AckFrameIF aframe= frame;  // Auxiliary variable to access the Ack frame fields.
            
            sim.cancel_data_timer( next_frame_to_send );
            next_frame_to_send = next_seq(next_frame_to_send);

            send_next_data_packet();
        }else{
            sim.Log("Error: not ACK or DATA\n");
        }
    }

    /**
     * CALLBACK FUNCTION: handle the end of the simulation
     * @param time current simulation time
     */
    @Override
    public void end_simulation(long time) {
        sim.Log("Stopping simulation\n");
    }
    
    private void send_next_data_packet() {
        
        CurrentPacket= net.from_network_layer(); //Pacote
        if (CurrentPacket != null /*Significa que nao ha mais*/ ) {
            send_packet(CurrentPacket);
        }
    }
    
    private void send_packet(String packet) {
        //Criar frame
        int ack = prev_seq(0);
        
        if( sim.isactive_ack_timer() ){
            sim.cancel_ack_timer();
            ack = ACK_frame.seq();
            sim.Log("PiggyBacking\n");
            if( ack != frame_expected ){
                ack = prev_seq(0);
                sim.Log("Error frame seq not expected\n"
                        + "Expected seq: "+frame_expected+"\n"
                                + "Frame recieved:"+ACK_frame.seq()+"\n");
            }
        }
        Frame frame= Frame.new_Data_Frame(next_frame_to_send /*seq*/, 
                                ack , 
                        net.get_recvbuffsize() /* returns the buffer space available in the network layer */,
                        packet);
        
        sim.to_physical_layer(frame, false /* do not interrupt an ongoing transmission*/);  
    }
    
    private DataFrameIF ACK_frame;
    private String CurrentPacket;
    private int next_frame_to_send;
    //private int prev_frame;
    private int frame_expected;
    /* Variables */
    
    /**
     * Reference to the simulator (Terminal), to get the configuration and send commands
     */
    //final Simulator sim;  -  Inherited from Base_Protocol
    
    /**
     * Reference to the network layer, to send a receive packets
     */
    //final NetworkLayer net;    -  Inherited from Base_Protocol
    
}
