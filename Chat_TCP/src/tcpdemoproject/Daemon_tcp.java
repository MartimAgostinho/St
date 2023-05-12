/*
 * Sistemas de Telecomunicacoes 
 *          2021/2022
 */
package tcpdemoproject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Thread that receives new connections in the ServerSocket
 *
 * @author lflb@fct.unl.pt
 */
public class Daemon_tcp extends Thread {

    volatile boolean isRunning = false;
    Chat_tcp root;                   // Main window object
    ServerSocket ss;                 // server socket

    /**
     * Constructor
     *
     * @param root - Main window object
     * @param _ss - Server socket
     */
    public Daemon_tcp(Chat_tcp root, ServerSocket _ss) {
        this.root = root;
        this.ss = _ss;
    }

    /**
     * Checks if thread is running
     *
     * @return true if it is running
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Main thread function - receives one connection and ends
     */
    @Override
    public void run() {
        isRunning = true;
        while( isRunning ){
            try {
                if (ss != null) {
                    Socket s = ss.accept();   // Wait for a new connection; s is a new Socket
                    Connection_tcp ct= root.start_connection_thread(s); // Ask Chat_tcp to start the connection thread
                    root.set_local_connection(ct); // set the local_conn variable value
                }
            } catch (IOException se) {  // Socket communication exception
                if (isRunning) {
                    root.Log_rem("recv TCP IOException : " + se + "\n");
                }
                this.stopRunning();
                
            } catch (Exception e) { // Other exception
                if (isRunning) {
                    root.Log_rem("Exception in Daemon_tcp : " + e);
                }
                this.stopRunning();
                
            /*} finally { // Always runs
                //this.stopRunning();
            }*/ 
        }
    }
    }

    /**
     * Stops main thread, interrupting the accept operation
     */
    public void stopRunning() {
        if (isRunning) {
            isRunning = false;
            this.interrupt();
        }
    }
}
