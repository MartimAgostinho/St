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
        // Initialize here all variables
        // ...
    }

    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     *
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nGo-Back-N Protocol\n\n");
        sim.Log("win_size: "+ win_size+"\n");
        
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
     * @param key  timer key (sequence number)
     */
    @Override
    public void handle_Data_Timer(long time, int key) {
        //sim.Log("handle_Data_Timer not implemented\n");
        
        roll_back_it( key );
        send_next_data_packet();
        //TODO: descobir Cancelar proximos data timers???
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
        if(frame.kind() == Frame.DATA_FRAME){
            
            //ver se ]e a data esperada
            //Ver se posso receber
            //comecar ack timer
            //Se nao for o esperado
                //Mandar NAK
                //Mudar frame expected -> prev_seq * Window size
            sim.Log("Data Frame\n");
            DataFrameIF dframe = frame;
            
            //sim.Log("Piggy \n"+prev_seq(next_dframe_ts)+"\n"+dframe.ack()+"\n"+add_seq(prev_seq( next_dframe_ts ), it_buff - 1) +"\n");

            if( between(
                    subt_seq(prev_seq( next_dframe_ts ), it_buff - 1) ,
                    dframe.ack(),
                    next_dframe_ts ) 
                    ){
                
                sim.Log("DEBUG: \nlower: "+next_dframe_ts+"\nupper: "+add_seq(next_dframe_ts, win_size)+"\n");
                sim.Log("Piggybacking! \n");

                ack_handler( dframe.ack() );
                send_next_data_packet();
            }
            
            if( dframe_expected == dframe.seq() ){//Verify if its the frame Im expecting
                
                sim.Log("Frame expected\n");
                sim.start_ack_timer();          //start/restart ack timer
                retrans_state = false;

                if (net.to_network_layer(dframe.info())){//if it works it works
                    dframe_expected = next_seq(dframe_expected);
                }
                
            //if im expecting frame n but I recieve n+1 i've lost frame n
            }else if( 
                    //is != dframe_expected but is in the window
                    between(dframe_expected  , dframe.seq() , add_seq(dframe_expected, win_size) ) 
                    && !retrans_state ){
            
                sim.Log("Sending NAK, frame was lost\n");
                send_NAK(dframe_expected);
               // sim.Log("dframe_ex"+dframe_expected+"\n");
                retrans_state = true;
            
            //ACK was lost
            }else if( !retrans_state ) {//antes era um else 

                send_ack();
                sim.Log("Ack frame was lost\n");
            }else{

                sim.Log("Retrans state\n");
            }

            
        }else if(frame.kind() == Frame.ACK_FRAME){
            
            AckFrameIF aframe = frame;
            sim.Log("ACK n:"+aframe.ack()+"\n");
            ack_handler( aframe.ack() );
            
            send_next_data_packet();

        }else if(frame.kind() == Frame.NAK_FRAME){

            NakFrameIF nframe = frame;

            if( nframe.nak() != seq_buff ){             
                ack_handler( prev_seq(nframe.nak() ));//ack all prev Data frames
            }
            
            roll_back_it( nframe.nak() );
            send_next_data_packet();
            
        }else{ sim.Log("Error: Not Data or ACK or NAK\n"); }
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

    private void send_NAK(int seq){

        sim.cancel_ack_timer();
        Frame frame = Frame.new_Nak_Frame(seq, net.get_recvbuffsize() );
        sim.to_physical_layer(frame, false);
        
    }
   
    private void send_ack(){
    
        Frame aframe = Frame.new_Ack_Frame( prev_seq( dframe_expected ), net.get_recvbuffsize());
        sim.to_physical_layer(aframe, false);
    
    }
    
    private void send_next_data_packet() {
        
        String tmp_frame = get_next_frame();
        
        if (tmp_frame == null ){ return; }
        
        send_Dpacket(tmp_frame);
        next_dframe_ts = next_seq(next_dframe_ts); 
    }
    
    private String get_next_frame(){
        
        if( it_buff >= win_size ){ //ja mendei todos os pacotes
            sim.Log("Buffer is full\n"); 
            return null;/*temporario*/
        }

        if( sending_buffer[it_buff] == null ){//Se nao houver pacote para mandar tenho de ir buscar a net

            String frame = net.from_network_layer(); //Pacote
            if( frame == null ){ return null; }
            sending_buffer[it_buff++] = frame;
            return frame;
        }
        
        return sending_buffer[it_buff++];
    }
    
     private void send_Dpacket(String packet) {
        //Criar frame
        
        sim.cancel_ack_timer();
        int ack = prev_seq(dframe_expected);
        
        Frame frame = Frame.new_Data_Frame(next_dframe_ts /*seq*/, 
                    ack/* ack= the one before 0 */, 
                    net.get_recvbuffsize() /* returns the buffer space available in the network layer */,
                    packet);
        
        sim.to_physical_layer(frame, false /* do not interrupt an ongoing transmission*/);
     }
    
    
     //rearrange buffer
     private void ack_handler(int seq){
     
         //se estiver fora da janela ja ackned 
         if( !between(
                    subt_seq(prev_seq( next_dframe_ts ), it_buff - 1) ,
                    seq,
                    next_dframe_ts )
                    ){
             sim.Log("Out of window\n"+prev_seq(next_dframe_ts)+seq+add_seq(prev_seq( next_dframe_ts ), it_buff - 1) +"\n");
             return;
         }
         
         String[] aux_buff = new String[ sending_buffer.length ];
         int dist = diff_seq( seq_buff,seq);
         int i;
         
         /*
         sim.Log("DEBUG ACK HANDLRE \n");
         for (String element: sending_buffer) { sim.Log(element);}
         sim.Log("\n info: \ninicio:"+(dist + 1)+"\ntamanho\n"+( win_size - (dist + 1) ));
         */
         
         //Shifts array dist unities to the left, other slots are now null
         System.arraycopy(sending_buffer, dist + 1  , aux_buff, 0 , win_size - (dist + 1) );
         
         //sim.Log("\nDEBUG ACK HANDLRE \n");
         //for (String element: sending_buffer) { sim.Log(element);}
         
        if( between(seq_buff  , seq , add_seq(seq_buff, win_size) ) ){         
            for( i = seq; i != seq_buff; i = prev_seq(i) ){
                sim.cancel_data_timer(i);
            }
         sim.cancel_data_timer(i);
        }
         
         it_buff -= dist + 1;
         sending_buffer = aux_buff;
         seq_buff = next_seq( seq );
     }
     
    //Updates it_buff for the right data_seq
    private void roll_back_it( int Data_seq ){
         //iterar sending_buff till == Data_seq

        sim.Log("Rolling back\n");
        //cancelar todos os timer desdo do data atual ate ao ultimo data que mandei
        for( int i = 0; i != sim.get_max_sequence(); i = next_seq(i) ){
            sim.cancel_data_timer(i);
        }
        sim.cancel_data_timer(7);

         it_buff = 0;
         next_dframe_ts = Data_seq;
         
    }

    /* Variables */
    
    private String[] sending_buffer;
    private int it_buff;                 //Iterador do buffer
    private int seq_buff;               //sequencia do primeiro pacote do buffer
    private int dframe_expected;       //
    private int next_dframe_ts;      //Sequence of the next data frame to send
    private int win_size;           //Window size
    private boolean retrans_state; //estado de retransmissao
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
