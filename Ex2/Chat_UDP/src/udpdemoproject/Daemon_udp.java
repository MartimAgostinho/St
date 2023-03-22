/*
 * Sistemas de Telecomunicacoes 
 *          2021/2022
 */
package udpdemoproject;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 *
 * @author lflb@fct.unl.pt
 */
public class Daemon_udp extends Thread {    
// inherits from Thread class
    private volatile boolean keepRunning;
    private final Chat_udp root;                        // Main window object
    private final DatagramSocket ds;                    // datagram socket

    /**
     * Constructor
     * @param _root  Main window object
     * @param _ds    Datagram socket
     */
    public Daemon_udp(Chat_udp _root, DatagramSocket _ds) {
        this.keepRunning = true;
        this.root = _root;
        this.ds = _ds;
    }

    /**
     *  Function run by the thread
     */
    @Override
    public void run() {
        byte[] buf = new byte[Chat_udp.MAX_PLENGTH];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        try {
            while (keepRunning) {
                try {
                    ds.receive(dp);    // Wait for packets
                    ByteArrayInputStream BAis =
                            new ByteArrayInputStream(buf, 0, dp.getLength());
                    DataInputStream dis = new DataInputStream(BAis);
                    
                    root.receive_packet(dp, dis);   // process packet in Chat_udp object
                    
                } catch (SocketException se) {
                    if (keepRunning) {
                        root.Log_rem("recv UDP SocketException : " + se + "\n");
                    }
                }
            }
        } catch (IOException e) {
            if (keepRunning) {
                root.Log_rem("IO exception receiving data from socket : " + e);
            }
        }
    }

    /**
     *  Stops loop by turning off keepRunning
     */
    public void stopRunning() {
        keepRunning = false;
    }
}
