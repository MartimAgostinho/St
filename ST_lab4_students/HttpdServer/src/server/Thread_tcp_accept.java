

/**
 * Sistemas de Telecomunicacoes 
 *          2022/2023
 *
 * Thread_tcp_accept.java
 *
 * Thread class that handles the acceptation of new connection on the server socket
 *
 * Created on March 20, 2022
 *
 * @author Luis Bernardo
 */
package server;

import java.net.ServerSocket;
import java.net.Socket;

class Thread_tcp_accept extends Thread {
    /**
     * GUI object
     */
    Web_server_main_thread main_thread;
    /**
     * Server socket object
     */
    ServerSocket ss;
    /**
     * Active flag
     */
    volatile boolean active;

    Thread_tcp_accept (Web_server_main_thread root, ServerSocket ss) {
        main_thread = root;
        this.ss = ss;
    }

    /**
     * Interrupt the thread
     */
    public void wake_up() {
        this.interrupt();
    }

    /**
     * Stop the thread
     */
    public void stop_thread() {
        active = false;
        this.interrupt();
    }

    /**
     * Thread's code
     */
    @Override
    public void run() {
        System.out.println(
                "\n******************** " + Web_server_main_thread.SERVER_NAME + " started ********************\n");
        active = true;
        while (active) {
            try {
                Socket s= ss.accept();  // Accepts a new connection from a browser
                Thread_tcp_connection conn = new Thread_tcp_connection (main_thread, s); // Creation connection thread object ...
                conn.start ();                                   // and start it
            } catch (java.io.IOException e) {
                if (active)
                    main_thread.Log("IO exception: " + e + "\n");
                active = false;
            }
        }
    }
} // end of class Thread_tcp_accept
