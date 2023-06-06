/*
 * Sistemas de Telecomunicacoes 
 *          2022/2023
 */
package protocol;

import terminal.Simulator;
import simulator.Frame;
import simulator.AckFrameIF;
import simulator.DataFrameIF;
import simulator.NakFrameIF;
import terminal.NetworkLayer;
import terminal.Terminal;

/**
 * Protocol 4 : Go-back-N protocol
 *
 * @author Martim Agostinho 62964
 */
public class GoBackN extends Base_Protocol implements Callbacks {

    public GoBackN(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol

        dframe_expected = 0;
        next_dframe_ts = 0;
        it_buff = 0;
        win_size = sim.get_send_window();//+1?
        retrans_state = false;
        sending_buffer = new String[win_size];
        seq_buff = 0;
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     *
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nGo-Back-N Protocol\n\n");

        send_next_data_packet();
    }

    /**
     * CALLBACK FUNCTION: handle the end of Data frame transmission, start timer
     * and send next until reaching the end of the sending window.
     *
     * @param time current simulation time
     * @param seq sequence number of the Data frame transmitted
     */
    @Override
    public void handle_Data_end(long time, int seq) {
        sim.start_data_timer(seq);
        send_next_data_packet();
    }

    /**
     * CALLBACK FUNCTION: handle the timer event; retransmit failed frames.
     *
     * @param time current simulation time
     * @param key timer key (sequence number)
     */
    @Override
    public void handle_Data_Timer(long time, int key) {
        roll_back_it(key);
        send_next_data_packet();
    }

    /**
     * CALLBACK FUNCTION: handle the ack timer event; send ACK frame
     *
     * @param time current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {

        send_ack();
    }

    /**
     * CALLBACK FUNCTION: handle the reception of a frame from the physical
     * layer
     *
     * @param time current simulation time
     * @param frame frame received
     */
    @Override
    public void from_physical_layer(long time, Frame frame) {
        //FALTA PIGGYBACKING
        sim.Log("Recieving frame, ");
        if (frame.kind() == Frame.DATA_FRAME) {
            
            sim.Log("Data Frame\n");
            DataFrameIF dframe = frame;

            if (dframe_expected == dframe.seq()) {//Verify if its the frame Im expecting
                
               //if it is I will check for piggyback
               if(sim.isactive_data_timer(dframe.ack())
                       && between(seq_buff, dframe.ack(), next_dframe_ts)) {

                   sim.Log("Piggybacking \n");

                   ack_handler(dframe.ack());
               }
                sim.Log("Frame expected\n");
                sim.start_ack_timer();          //start/restart ack timer
                retrans_state = false;

                if (net.to_network_layer(dframe.info())) {
                    dframe_expected = next_seq(dframe_expected);
                }

                //if im expecting frame n but I recieve n+1 i've lost frame n
               //This condition checks for that
            } else if (//is != dframe_expected but is in the window
                    between(dframe_expected,
                            dframe.seq(),
                            add_seq(dframe_expected, win_size)) //AS JANELAS TEEM DE SER IGUAIS DOS DOIS LADOS
                    && !retrans_state /*&& sim.isactive_data_timer( frame.nak() )*/) {

                sim.Log("Sending NAK, frame was lost\n");
                send_NAK(dframe_expected);
                retrans_state = true;
                //ACK was lost
            } else if (!retrans_state) {

                send_ack();
                sim.Log("Ack frame was lost\n");
            } else {

                send_ack();//it solves a problem where Im receiving data out of the window
                sim.Log("Retrans state\n");
            }

        } else if (frame.kind() == Frame.ACK_FRAME) {

            AckFrameIF aframe = frame;
            
            sim.Log("ACK n:" + aframe.ack() + "\n");
            ack_handler(aframe.ack());
            send_next_data_packet();

        } else if (frame.kind() == Frame.NAK_FRAME) {

            NakFrameIF nframe = frame;

            //If NAK is for a Data packet that is not active I will discard it 
            if (!sim.isactive_data_timer(nframe.nak()) ){
                sim.Log("Nack Out of window\n" + seq_buff + " " + nframe.nak() + " " + next_dframe_ts + "\n");
                return;
            }
            
            if (nframe.nak() != seq_buff) {
                ack_handler(prev_seq(nframe.nak()));//ack all prev Data frames
            }

            roll_back_it(nframe.nak());
            send_next_data_packet();

        } else {
            sim.Log("Error: Not Data or ACK or NAK\n");
        }
    }

    /**
     * CALLBACK FUNCTION: handle the end of the simulation
     *
     * @param time current simulation time
     */
    @Override
    public void end_simulation(long time) {
        sim.Log("Stopping simulation\n");
    }

    /*
    Description: Sends a NAK with sequence = int seq
    Args:
        seq = NAK's sequence
    Return:

    */
    private void send_NAK(int seq) {

        if (sim.is_sending_data()) {
            return;
        }
        sim.cancel_ack_timer();
        Frame frame = Frame.new_Nak_Frame(seq, net.get_recvbuffsize());
        sim.to_physical_layer(frame, false);

    }

    /*
    Description: Sends an ACK 
    Args:
    Return:
    
    */
    private void send_ack() {

        if (sim.is_sending_data()) {
            return;
        }
        Frame aframe = Frame.new_Ack_Frame(prev_seq(dframe_expected), net.get_recvbuffsize());
        sim.to_physical_layer(aframe, false);

    }

    /*
    Description: Sends the next data packet
    Args:
    Return:
    
    */
    private void send_next_data_packet() {

        if (sim.is_sending_data()) {
            return;
        }
        String tmp_frame = get_next_frame();

        if (tmp_frame == null) {
            return;
        }

        send_Dpacket(tmp_frame);
        next_dframe_ts = next_seq(next_dframe_ts);
    }

    /*
    Description: Returns the next data frame to send
    Args:
    Return: Next Frame to send
    
    */
    private String get_next_frame() {

        if (it_buff >= win_size) { //ja mandei todos os pacotes
            sim.Log("Buffer is full\n");
            return null;/*temporario*/
        }

        if (sending_buffer[it_buff] == null) {//Se nao houver pacote para mandar tenho de ir buscar a net

            String frame = net.from_network_layer(); //Pacote
            if (frame == null) {
                return null;
            }
            sending_buffer[it_buff++] = frame;
            return frame;
        }

        return sending_buffer[it_buff++];
    }

    /*
    Description: Sends a Data packet
    Args: packet to send
    Return:
    
    */
    private void send_Dpacket(String packet) {

        sim.cancel_ack_timer();
        int ack = prev_seq(dframe_expected);

        Frame frame = Frame.new_Data_Frame(next_dframe_ts /*seq*/,
                ack/* ack= the one before 0 */,
                net.get_recvbuffsize() /* returns the buffer space available in the network layer */,
                packet);

        sim.to_physical_layer(frame, false /* do not interrupt an ongoing transmission*/);
    }

    /*
    Description: Handles the ack wih sequence number "seq"
    Args: seq -> ack sequence number
    Return: 
    
    */    
    private void ack_handler(int seq) {

        //Verify if is already ackned
        if (!sim.isactive_data_timer(seq)) {
            sim.Log("Ack Already handled\n");
            return;
        }

        String[] aux_buff = new String[sending_buffer.length];
        int dist = diff_seq(seq_buff, seq);
        int i;

        //Shifts array dist unities to the left, other slots are now null
        System.arraycopy(sending_buffer, dist + 1, aux_buff, 0, win_size - (dist + 1));

        //if is between 
        if (between(seq_buff, seq, add_seq(seq_buff, win_size))) {
            for (i = seq; i != seq_buff; i = prev_seq(i)) {
                sim.cancel_data_timer(i);
            }
            sim.cancel_data_timer(i);
        }

        it_buff -= dist + 1;
        sending_buffer = aux_buff;
        seq_buff = next_seq(seq);
    }

    /*
    Description: Updates it_buff for the right data_seq
    Args: Sequence of the Data I want to start resending
    Return: 
    
    */
    private void roll_back_it(int Data_seq) {
        //iterar sending_buff till == Data_seq

        sim.Log("Rolling back\n");
        //Cancels all data timers
        for (int i = 0; i != sim.get_max_sequence(); i = next_seq(i)) {
            sim.cancel_data_timer(i);
        }
        sim.cancel_data_timer(7);

        it_buff = 0;
        next_dframe_ts = Data_seq;
    }

    /* Variables */
    private String[] sending_buffer;     //Buffer where I store my Data frames
    private int it_buff;                //It points to a position in the buffer, this position contains the next frame to send
    private int seq_buff;              //First packet sequence
    private int dframe_expected;      //Sequence of the data Im expecting to receive
    private int next_dframe_ts;      //Sequence of the next data frame to send
    private int win_size;           //Window size
    private boolean retrans_state; //Is active in the time between sending a NAK and receiving the right data frame expected
    /* Variables */

    
    /**
     * Reference to the simulator (Terminal), to get the configuration and send
     * commands
     */
    //final Simulator sim;  -  Inherited from Base_Protocol
    /**
     * Reference to the network layer, to send a receive packets
     */
    //final NetworkLayer net;    -  Inherited from Base_Protocol
}
