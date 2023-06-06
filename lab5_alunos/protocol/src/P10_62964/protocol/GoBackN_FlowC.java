/*
 * Sistemas de Telecomunicacoes 
 *          2022/2023
 */
package protocol;

import simulator.AckFrameIF;
import simulator.DataFrameIF;
import terminal.Simulator;
import simulator.Frame;
import simulator.NakFrameIF;
import terminal.NetworkLayer;
import terminal.Terminal;

/**
 * Protocol 5 : Go-back-N protocol with flow control (buffer size)
 *
 * @author Martim Duarte Agostinho (62964)
 */
public class GoBackN_FlowC extends Base_Protocol implements Callbacks {

    public GoBackN_FlowC(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol

        dframe_expected = 0;
        next_dframe_ts = 0;
        it_buff = 0;
        win_size = sim.get_send_window();
        retrans_state = false;
        sending_buffer = new String[sim.get_send_window()];
        seq_buff = 0;
        rcv_buff = 0;
        first_packet = true;
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     *
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nGo-Back-N Protocol\n\n");

        // sim.Log("\nNot implemented yet\n\n");
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
        //sim.Log("handle_Data_end not implemented\n");
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
        //sim.Log("handle_Data_Timer not implemented\n");

        if( rcv_buff > 0 && net.has_more_packets_to_send() ){
            roll_back_it(key);
        }
        send_next_data_packet();
    }

    /**
     * CALLBACK FUNCTION: handle the ack timer event; send ACK frame
     *
     * @param time current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {
        //sim.Log("handle_ack_Timer not implemented\n");

        send_ack();
        //send last ack 
    }

    /**
     * Event generated when new network buffers are available
     *
     * @param time current simulation time
     * @param old_numberofbuffers number of buffers before update
     * @param current_numberofbuffers number of buffers after update
     *
     */
    public void new_network_buffers(long time, int old_numberofbuffers, int current_numberofbuffers) {
        sim.start_ack_timer();
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

        rcv_buff = frame.rcvbufsize();

        if (frame.kind() == Frame.DATA_FRAME) {

            sim.Log("Data Frame\n");
            DataFrameIF dframe = frame;

            if (dframe_expected == dframe.seq()) {//Verify if its the frame Im expecting

                if (sim.isactive_data_timer(dframe.ack())
                        && between(seq_buff, dframe.ack(), next_dframe_ts)) {

                    sim.Log("Piggybacking! \n");

                    ack_handler(dframe.ack() );
                }

                sim.Log("Frame expected\n");
                sim.start_ack_timer();          //start/restart ack timer
                retrans_state = false;

                if (net.to_network_layer(dframe.info())) {//if it works it works
                    dframe_expected = next_seq(dframe_expected);
                }

                //if im expecting frame n but I receive n+1 i've lost frame n
            } else if (//is != dframe_expected but is in the window
                    between(dframe_expected,
                            dframe.seq(),
                            add_seq(dframe_expected, win_size)) //AS JANELAS TEEM DE SER IGUAIS DOS DOIS LADOS
                    && !retrans_state /*&& sim.isactive_data_timer( frame.nak() )*/) {

                sim.Log("Sending NAK, frame was lost\n");
                send_NAK(dframe_expected);
                // sim.Log("dframe_ex"+dframe_expected+"\n");
                retrans_state = true;
                //ACK was lost
            } else if (!retrans_state) {//antes era um else 

                send_ack();
                sim.Log("Ack frame was lost\n");
            } else {

                send_ack();
                sim.Log("Retrans state\n");
            }

        } else if (frame.kind() == Frame.ACK_FRAME) {

            AckFrameIF aframe = frame;
            int ack = aframe.ack();
            
            if( rcv_buff == 0 && net.has_more_packets_to_send()){
            
                ack = prev_seq(ack);
            }
            
            ack_handler(ack);
            send_next_data_packet();

        } else if (frame.kind() == Frame.NAK_FRAME) {

            NakFrameIF nframe = frame;

            int nak = nframe.nak();

            if (rcv_buff == 0 && net.has_more_packets_to_send()) {
                nak = prev_seq(nak);
            }

            if (!sim.isactive_data_timer(nak)) {
                sim.Log("Nack Out of window\n" + seq_buff + " " + nak + " " + next_dframe_ts + "\n");
                return;
            }

            if (nak != seq_buff) {
                ack_handler(prev_seq(nframe.nak()));//ack all prev Data frames
            }

            roll_back_it(nak);
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

    private void send_NAK(int seq) {

        if (sim.is_sending_data()) {
            return;
        }
        sim.cancel_ack_timer();
        Frame frame = Frame.new_Nak_Frame(seq, net.get_recvbuffsize());
        sim.to_physical_layer(frame, false);
    }

    private void send_ack() {

        if (sim.is_sending_data()) {
            return;
        }
        Frame aframe = Frame.new_Ack_Frame(prev_seq(dframe_expected), net.get_recvbuffsize() );
        sim.to_physical_layer(aframe, false);

    }

    private void send_next_data_packet() {

        if (sim.is_sending_data()) {
            return;
        }
        String tmp_frame = get_next_frame();

        if (tmp_frame == null) {
            return;
        }
        send_Dpacket(tmp_frame);

    }

    private String get_next_frame() {

        if( !net.has_more_packets_to_send() ){ return null; }
        if ( (it_buff >= win_size ) ||
                ( it_buff > rcv_buff  && rcv_buff != 0 && net.has_more_packets_to_send() )
            ) { //ja mandei todos os pacotes
            sim.Log("This Buffer is full\n");
            return null;
        
        }

        if (rcv_buff == 0){

            if (first_packet){

                first_packet = false;
                String frame = net.from_network_layer(); //Pacote
                if (frame == null) {
                    return null;
                }
                sending_buffer[it_buff] = frame;
                return frame;
            }
            
            //if it has more packets to send, sends the last one
            return sending_buffer[it_buff];
        }
        
        if( it_buff < 0 && sending_buffer[0] != null){
            it_buff = 1;
        }

        if (sending_buffer[it_buff] == null) {//Se nao houver pacote para mandar tenho de ir buscar a net

            String frame = net.from_network_layer(); //Pacote
            if (frame == null) {
                return null;
            }
            sending_buffer[it_buff++] = frame;
            next_dframe_ts = next_seq(next_dframe_ts);
            return frame;
        }
        next_dframe_ts = next_seq(next_dframe_ts);
        return sending_buffer[it_buff++];
    }

    private void send_Dpacket(String packet) {
        //Criar frame

        if( sim.isactive_data_timer(next_dframe_ts) ){ return; }
        sim.cancel_ack_timer();
        int ack = prev_seq(dframe_expected);

        int seq = next_dframe_ts;
        Frame frame = Frame.new_Data_Frame( seq /*seq*/,
                ack/* ack= the one before 0 */,
                net.get_recvbuffsize()/* returns the buffer space available in the network layer */,
                packet);

        sim.to_physical_layer(frame, false /* do not interrupt an ongoing transmission*/);
    }

    //rearrange buffer
    private void ack_handler(int seq) {

        //Verify if is already ackned or the rcv buff is full
        if ( !sim.isactive_data_timer(seq) ){
            sim.Log("Ack Already handled\n");
            return;
        }
        
        if( rcv_buff == 0 && net.has_more_packets_to_send()  ){
        
            sim.Log("The rcv buff is full\n");
            return;
        }
        
        String[] aux_buff = new String[sending_buffer.length];
        int dist = diff_seq(seq_buff, seq);
        int i;

        //Shifts array dist unities to the left, other slots are now null
        System.arraycopy(sending_buffer, dist + 1, aux_buff, 0, win_size - (dist + 1));

        if (between(seq_buff, seq, add_seq(seq_buff, win_size)) && rcv_buff > 0) {
            for (i = seq; i != seq_buff; i = prev_seq(i)) {
                sim.cancel_data_timer(i);
            }
            sim.cancel_data_timer(i);

        }else if (between(seq_buff, seq, add_seq(seq_buff, win_size))) {
            //I will ack all the data timers except the last one
            for (i = seq; i != seq_buff; i = prev_seq(i)) {
                sim.cancel_data_timer(i);
            }
        }

        it_buff -= dist + 1;
        sim.Log(it_buff+" itbuff\n");
        print_sb();
        sending_buffer = aux_buff;
        print_sb();

        seq_buff = next_seq(seq);
    }

    //Updates it_buff for the right data_seq
    private void roll_back_it(int Data_seq) {
        //iterar sending_buff till == Data_seq

        print_sb();
        sim.Log("Rolling back\n");
        //cancelar todos os timer desdo do data atual ate ao ultimo data que mandei
        for (int i = 0; i != sim.get_max_sequence(); i = next_seq(i)) {
            sim.cancel_data_timer(i);
        }
        sim.cancel_data_timer(7);

        it_buff = 0;
        next_dframe_ts = Data_seq;
    }

    /* Variables */
    private String[] sending_buffer;
    private int it_buff;                //Iterador do buffer
    private int seq_buff;              //sequencia do primeiro pacote do buffer
    private int dframe_expected;      //
    private int next_dframe_ts;      //Sequence of the next data frame to send
    private int win_size;           //Window size
    private boolean retrans_state; //estado de retransmissao
    private int rcv_buff;         //buffer size of the receiver
    private boolean first_packet;//special condition if im sending the first packet
    /**
     * Reference to the simulator (Terminal), to get the configuration and send
     * commands
     */
    //final Simulator sim;  -  Inherited from Base_Protocol
    /**
     * Reference to the network layer, to send a receive packets
     */
    //final NetworkLayer net;    -  Inherited from Base_Protocol
    
    //DEBUG
    private void print_sb(){
        
        sim.Log("DEBUG\n");
        for (String element: sending_buffer) {
            sim.Log(element+"\n");
        } 
        sim.Log("DEBUG\n");

    }
    
}
