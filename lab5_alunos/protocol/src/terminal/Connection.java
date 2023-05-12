/*
 * Sistemas de Telecomunicacoes 
 *          2022/2023
 */
package terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Thread that handles the communication with a remote channel
 * It handles message reception and provides message sending
 * 
 * @author lflb@fct.unl.pt
 */
public class Connection extends Thread {

    volatile boolean keepRunning = true;
    Terminal root;           // Main window object
    Socket s;               // socket
    PrintStream pout;       // Device used to write strings to the socket
    BufferedReader in;      // Device used to read from socket

    Connection(Terminal _root, Socket _s) {
        this.root = _root;
        this.s = _s;
    }
    
    /**
     * Returns a string with the IP:port of the remote host
     * @return string with the IP:port of the remote host
     */
    @Override
    public String toString() {
        if ((s==null) || !s.isConnected()) {
            return "null";
        }
        return s.getInetAddress().getHostAddress()+":"+s.getPort();
    } 
    
    /**
     * Sends a message using the connection
     * @param msg
     * @return true in case of success
     */
    public boolean send_message(String msg) {
        try {
            if (Terminal.debug) {
                root.Log("Sent message: "+msg+"\n");
            }
            pout.print(msg + "\n");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public void run() {
        try {
            String message;
            in = new BufferedReader(
                    new InputStreamReader(s.getInputStream(), "8859_1"));
            OutputStream out= s.getOutputStream();
            pout = new PrintStream(out, false, "8859_1");
            // First read the station name
            message= in.readLine();
            root.set_name(message);
            // Then loop for events
            while (keepRunning) {   // Loop waiting for messages
                message = in.readLine();    // Blocks waiting for new messages
                if (message == null) {
                    // End of connection
                    return;
                }
                root.receive_message(message);  // Calls Chat_tcp object
            }
        } catch (IOException e) {   // Catches comminication errors
            if (keepRunning) {
                System.out.println("I/O error " + e);
            }
        } catch (Exception e) { // Catches all other errors
            if (keepRunning) {
                System.out.println("Error " + e);
            }
        } finally {   // Always runs this code
            try {
                s.close();   // Closes the socket and all devices associated
            } catch (Exception e) { /* Ignore everything */ }
            root.connection_thread_ended(this);
        }
    }
    
    /**
     * Stops the Connection thread, closing the socket and turning off KeepRunning
     */
    public void stopRunning() {
        keepRunning = false;
        try {
            s.close();   // Closes the socket and all devices associated
        } catch (Exception e) { 
            System.err.println("Error closing socket: "+e); 
            /* Ignore everything */ 
        }
        this.interrupt();  // Forces the thread to interrupt any blocking call
    }
    
} // end of class Connection
