/*
 * Sistemas de Telecomunicacoes 
 *          2022/2023
 */
package protocol;

import terminal.Simulator;
import simulator.Frame;
import terminal.NetworkLayer;

/**
 * Protocol 1 sender : Utopian protocol that does not retransmit failed frames
 * 
 * @author lflb@fct.unl.pt
 */
public class Utopian_snd extends Base_Protocol implements Callbacks {

    public Utopian_snd(Simulator _sim, NetworkLayer _net) {
        super(_sim, _net);      // Calls the constructor of Base_Protocol
        next_frame_to_send = 0;     // First frame sent is 0
    }

    
    /**
     * Fetches the network layer for the next packet and starts it transmission
     * @return true is started data frame transmission, false otherwise
     */
    private void send_next_data_packet() {
        // We can only send one Data packet at a time
        //   you must wait for the DATA_END event before transmitting another one
        //   otherwise the first packet is lost in the channel
        String packet= net.from_network_layer();
        if (packet != null) {
            // The ACK field of the DATA frame is always the sequence number before zero, because no packets will be received
            Frame frame = Frame.new_Data_Frame(next_frame_to_send /*seq*/, 
                    prev_seq(0) /* ack= the one before 0 */, 
                    net.get_recvbuffsize() /* returns the buffer space available in the network layer */,
                    packet);
            sim.to_physical_layer(frame, false /* do not interrupt an ongoing transmission*/);
            next_frame_to_send = next_seq(next_frame_to_send);
            // Transmission of next DATA frame occurs after DATA_END event is received
        }
    }
    
    /**
     * CALLBACK FUNCTION: handle the beginning of the simulation event
     * @param time current simulation time
     */
    @Override
    public void start_simulation(long time) {
        sim.Log("\nUtopian Protocol - sender\n\n");
        send_next_data_packet();    // Start sending the first packet
    }

    /**
     * CALLBACK FUNCTION: handle the end of Data frame transmission, and start 
     * sending the next frame
     * @param time current simulation time
     * @param seq  sequence number of the Data frame transmitted
     */
    @Override
    public void handle_Data_end(long time, int seq) {
        // When the first packet is transmitted, send the next one
        send_next_data_packet();
    }

    /**
     * CALLBACK FUNCTION: handle the timer event; ignores it in this case
     * @param time current simulation time
     * @param key   timer key (sequence number)  
     */
    @Override
    public void handle_Data_Timer(long time, int key) {
        sim.Log(time + " Data Timeout not expected\n");
    }

    /**
     * CALLBACK FUNCTION: handle the ack timer event; ignores it in this case
     * @param time current simulation time
     */
    @Override
    public void handle_ack_Timer(long time) {
        sim.Log(time + " ACK Timeout not expected\n");
    }
    
    /**
     * CALLBACK FUNCTION: handle the reception of a frame from the physical layer
     * @param time current simulation time
     * @param frame frame received
     */
    @Override
    public void from_physical_layer(long time, Frame frame) {
        sim.Log(time + " protocol Utopian_snd received: " + frame.toString() +" - not expected\n");
    }

    /**
     * CALLBACK FUNCTION: handle the end of the simulation
     * @param time current simulation time
     */
    @Override
    public void end_simulation(long time) {
        sim.Log("Stopping simulation\n");
    }
    
    
    /* Variables */
    
    /**
     * Reference to the simulator (Terminal), to get the configuration and send commands
     */
    //final Simulator sim;  -  Inherited from Base_Protocol
    
    /**
     * Reference to the network layer, to send a receive packets
     */
    //final NetworkLayer net;    -  Inherited from Base_Protocol
    
    /**
     * Sequence number of the next data frame
     */
    private int next_frame_to_send;    
}
