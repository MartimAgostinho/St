/*
 * Sistemas de Telecomunicacoes 
 *          2021/2022
 */
package tcpdemoproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Thread that handles the communication with a remote chat_tcp user
 * It handles message reception and provides message sending
 * 
 * @author lflb@fct.unl.pt
 */
public class Connection_tcp extends Thread {

    volatile boolean keepRunning = true;
    Chat_tcp root;          // Main window object
    Socket s;               // socket
    PrintStream pout;       // Device used to write strings to the socket
    BufferedReader in;      // Device used to read from the socket

    Connection_tcp(Chat_tcp _root, Socket _s) {
        this.root = _root;
        this.s = _s;
    }
    
    /**
     * Returns the remote’s ID (a string with the IP:port of the remote host)
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
            pout.print(msg + "\n");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    @Override public void run() {
        try {
            String message;
            in = new BufferedReader(
                    new InputStreamReader(s.getInputStream(), "8859_1"));
            pout = new PrintStream(s.getOutputStream(), false, "8859_1");

            while (keepRunning) {   // Loop waiting for messages
                message = in.readLine();    // Blocks waiting for new messages (lines of text)
                if (message == null) {
                    // End of connection
                    return;
                }
                root.receive_message(this, message);  // Calls Chat_tcp object
            }
        } catch (IOException e) {   // Catches communication errors
            if (keepRunning) {
                System.out.println("I/O error " + e);
            }
        } catch (Exception e) { // Catches all other errors
            if (keepRunning) {
                System.out.println("Error " + e);
            }
        } finally {   // Always runs this code, even when return is called!
            try {
                s.close();   // Closes the socket and all devices associated
            } catch (Exception e) { /* Ignore everything */ }
            root.connection_thread_ended(this); // Inform Chat_tcp object that thread ended
        }
    }
    
    public boolean send_file(File f) { // Should be public because is called by Chat_tcp
        FileInputStream fis = null;
        try {
            fis = new FileInputStream (f); // Open file input stream
            byte[] buffer= new byte[fis.available()];// allocate a buffer with the
            // length of the file
            int n= fis.read(buffer); // read the entire file to buffer;
            //n counts the number of bytes actually read
            if (n != buffer.length) {
            root.Log_loc("Did not read the entire file\n");
            return false;
            }
            pout.write(buffer, 0, n); // write to socket
            return true;
        } catch (IOException ex) {
            root.Log_loc("Error sending file "+f+"\n");
            return false;
        } finally {
            try {
                fis.close(); // Always close the file
            } catch (IOException ex) { /* Ignore error */ }
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
            System.err.println("Warning closing socket: "+e); 
            /* Ignore everything */ 
        }
        this.interrupt();  // Forces the thread to interrupt any blocking call
    }
    
} // end of class Connection_tcp
