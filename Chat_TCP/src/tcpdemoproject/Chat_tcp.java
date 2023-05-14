/*
 * Sistemas de Telecomunicacoes 
 *          2021/2022
 */
package tcpdemoproject;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;

/**
 * Main module; defines the window interaction, socket creation and application
 * logic
 *
 * @author lflb@fct.unl.pt
 */
public class Chat_tcp extends javax.swing.JFrame {
    
    private HashMap<String, Connection_tcp> connlist = new HashMap<String, Connection_tcp>();
    
    /**
     * Creates new form Chat_tcp
     */
    public Chat_tcp() {
        initComponents();   // defined by NetBeans, creates the graphical window
        ss = null;          // Set null value – meaning “not initialized”
        serv_tcp = null;        // Set null value – meaning “not initialized”
        local_connection = null;        // Set null value – meaning “not initialized”
        try {
            // Get local IP and set port to 0
            InetAddress addr = InetAddress.getLocalHost(); // Get the local IP address
            jTextLocIP.setText(addr.getHostAddress());      // Set the IP text fields to 
            jTextRemIP.setText(addr.getHostAddress());      //    the local address
        } catch (UnknownHostException e) {
            System.err.println("Unable to determine local IP address: " + e);
            System.exit(-1);    // Closes the application
        }
        jTextLocPort.setText("20000");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jPanelLocal = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextLocIP = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextLocPort = new javax.swing.JTextField();
        jToggleButtonActive = new javax.swing.JToggleButton();
        jButtonClear = new javax.swing.JButton();
        jPanelRemote = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextRemIP = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextRemPort = new javax.swing.JTextField();
        jToggleButtonConnect = new javax.swing.JToggleButton();
        jPanelMessage = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTextMessage = new javax.swing.JTextField();
        jButtonSend = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabelLocal = new javax.swing.JLabel();
        jScrollPaneLocal = new javax.swing.JScrollPane();
        jTextAreaLocal = new javax.swing.JTextArea();
        jLabelRemote = new javax.swing.JLabel();
        jScrollPaneRemote = new javax.swing.JScrollPane();
        jTextAreaRemote = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat TCP");
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelLocal.setMaximumSize(new java.awt.Dimension(550, 40));
        jPanelLocal.setMinimumSize(new java.awt.Dimension(197, 38));
        jPanelLocal.setPreferredSize(new java.awt.Dimension(550, 38));

        jLabel3.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        jLabel3.setText("Local: ");
        jPanelLocal.add(jLabel3);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jLabel1.setText("IP");
        jPanelLocal.add(jLabel1);

        jTextLocIP.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTextLocIP.setText("127.0.0.1");
        jTextLocIP.setMaximumSize(new java.awt.Dimension(120, 28));
        jTextLocIP.setPreferredSize(new java.awt.Dimension(120, 28));
        jTextLocIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextLocIPActionPerformed(evt);
            }
        });
        jPanelLocal.add(jTextLocIP);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jLabel2.setText("Port");
        jPanelLocal.add(jLabel2);

        jTextLocPort.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTextLocPort.setText("20000");
        jTextLocPort.setMaximumSize(new java.awt.Dimension(60, 28));
        jPanelLocal.add(jTextLocPort);

        jToggleButtonActive.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jToggleButtonActive.setText("Active");
        jToggleButtonActive.setMaximumSize(new java.awt.Dimension(85, 30));
        jToggleButtonActive.setPreferredSize(new java.awt.Dimension(85, 29));
        jToggleButtonActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonActiveActionPerformed(evt);
            }
        });
        jPanelLocal.add(jToggleButtonActive);

        jButtonClear.setBackground(new java.awt.Color(220, 220, 100));
        jButtonClear.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jButtonClear.setForeground(new java.awt.Color(76, 76, 28));
        jButtonClear.setText("Clear");
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jPanelLocal.add(jButtonClear);

        getContentPane().add(jPanelLocal);

        jPanelRemote.setMaximumSize(new java.awt.Dimension(570, 40));
        jPanelRemote.setPreferredSize(new java.awt.Dimension(570, 38));

        jLabel6.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        jLabel6.setText("Remote: ");
        jPanelRemote.add(jLabel6);

        jLabel7.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jLabel7.setText("IP");
        jPanelRemote.add(jLabel7);

        jTextRemIP.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTextRemIP.setText("127.0.0.1");
        jTextRemIP.setMaximumSize(new java.awt.Dimension(120, 28));
        jTextRemIP.setPreferredSize(new java.awt.Dimension(120, 28));
        jPanelRemote.add(jTextRemIP);

        jLabel8.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        jLabel8.setText("Port");
        jPanelRemote.add(jLabel8);

        jTextRemPort.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTextRemPort.setText("20000");
        jPanelRemote.add(jTextRemPort);

        jToggleButtonConnect.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jToggleButtonConnect.setText("Connect");
        jToggleButtonConnect.setMaximumSize(new java.awt.Dimension(100, 30));
        jToggleButtonConnect.setPreferredSize(new java.awt.Dimension(90, 30));
        jToggleButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonConnectActionPerformed(evt);
            }
        });
        jPanelRemote.add(jToggleButtonConnect);

        getContentPane().add(jPanelRemote);

        jPanelMessage.setMaximumSize(new java.awt.Dimension(570, 40));
        jPanelMessage.setMinimumSize(new java.awt.Dimension(156, 38));
        jPanelMessage.setPreferredSize(new java.awt.Dimension(570, 38));

        jLabel9.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        jLabel9.setText("Message: ");
        jPanelMessage.add(jLabel9);

        jTextMessage.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTextMessage.setText("Hello!");
        jTextMessage.setMaximumSize(new java.awt.Dimension(2147483647, 28));
        jTextMessage.setPreferredSize(new java.awt.Dimension(200, 28));
        jPanelMessage.add(jTextMessage);

        jButtonSend.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jButtonSend.setText(" Send ");
        jButtonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendActionPerformed(evt);
            }
        });
        jPanelMessage.add(jButtonSend);

        getContentPane().add(jPanelMessage);

        jButton1.setText("Send File");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);

        jLabelLocal.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabelLocal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelLocal.setText("Local");
        getContentPane().add(jLabelLocal);

        jTextAreaLocal.setColumns(20);
        jTextAreaLocal.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTextAreaLocal.setRows(5);
        jScrollPaneLocal.setViewportView(jTextAreaLocal);

        getContentPane().add(jScrollPaneLocal);

        jLabelRemote.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N
        jLabelRemote.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelRemote.setText("Remote");
        getContentPane().add(jLabelRemote);

        jTextAreaRemote.setColumns(20);
        jTextAreaRemote.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jTextAreaRemote.setRows(5);
        jScrollPaneRemote.setViewportView(jTextAreaRemote);

        getContentPane().add(jScrollPaneRemote);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Handle "Active" button; starts and stops the chat application
     */
    private void jToggleButtonActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonActiveActionPerformed
        if (jToggleButtonActive.isSelected()) {    // The button is ON                                                    
            int port;
            try {   // Read the port number in Local Port text field
                port = Integer.parseInt(jTextLocPort.getText());
            } catch (NumberFormatException e) {
                Log_loc("Invalid local port number: " + e + "\n");
                jToggleButtonActive.setSelected(false); // Set the button off
                return;
            }
            try {
                ss = new ServerSocket(port);    // Create the TCP Server socket
                jTextLocPort.setText("" + ss.getLocalPort());
                jTextRemPort.setText("" + (ss.getLocalPort() + 1));
                //
                serv_tcp = new Daemon_tcp(this, ss);  // Create the connection receiver thread
                serv_tcp.start();                     // Start the thread
                Log_loc("Chat_tcp active\n");
            } catch (IOException e) {
                Log_loc("Socket creation failure: " + e + "\n");
                jToggleButtonActive.setSelected(false); // Set the button off
            }
        } else {    // The button is OFF
            
            for (Connection_tcp c: connlist.values()) {
                if (serv_tcp != null) {     // If connection receiver thread is running
                    serv_tcp.stopRunning(); // Stop the thread
                    serv_tcp = null;        // Thread will be garbadge collected after it stops
                }
                if (c != null) {     // If connection thread is running
                    c.stopRunning(); // Stop the thread
                    c = null;        // Thread will be garbadge collected after it stops
                }
            }
            
            if (serv_tcp != null) {     // If connection receiver thread is running
                serv_tcp.stopRunning(); // Stop the thread
                serv_tcp = null;        // Thread will be garbadge collected after it stops
            }
            if (local_connection != null) {     // If connection thread is running
                local_connection.stopRunning(); // Stop the thread
                local_connection = null;        // Thread will be garbadge collected after it stops
            }
            if (ss != null) {       // If server socket is active
                try {
                    ss.close();     // Close the socket
                } catch (IOException ex) {
                    // Ignore
                }
                ss = null; // Forces garbadge collecting
            }
            
            Log_loc("Chat_tcp stopped\n");
        }
        connlist.clear();
    }//GEN-LAST:event_jToggleButtonActiveActionPerformed

    /**
     * Handle button "Send" - sends a message
     */
    private void jButtonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendActionPerformed
        send_packet();
    }//GEN-LAST:event_jButtonSendActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        jTextAreaLocal.setText("");
        jTextAreaRemote.setText("");
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jToggleButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonConnectActionPerformed
        if (jToggleButtonConnect.isSelected()) {
            // The button is ON - Start the connection
            if (local_connection != null) { // A Connection is active; ignore request
                return;
            }
            InetAddress netip;
            try { // Test IP address in Remote IP text box
                netip = InetAddress.getByName(jTextRemIP.getText());
            } catch (UnknownHostException e) {
                Log_loc("Invalid remote host address: " + e + "\n");
                jToggleButtonConnect.setSelected(false);
                return;
            }
            int port;
            try { // Test port
                port = Integer.parseInt(jTextRemPort.getText());
            } catch (NumberFormatException e) {
                Log_loc("Invalid remote port number: " + e + "\n");
                jToggleButtonConnect.setSelected(false);
                return;
            }
            try {
                Socket cs = new Socket(netip, port); // Create and connect a socket to the remote
                set_local_connection(start_connection_thread(cs));    // Start the connection thread
                //serv_tcp.stopRunning();             // Stop connection receive thread
                //serv_tcp = null;
            } catch (Exception ex) {
                Log_loc("Connection to " + jTextRemIP.getText() + ":" + jTextRemPort.getText() + " failed\n");
                jToggleButtonConnect.setSelected(false);
            }

        } else {

            // The button is OFF – stop the connection
            if (local_connection != null) {
                local_connection.stopRunning(); // Stop the connection
                
                set_local_connection(null);
            }
        }
    }//GEN-LAST:event_jToggleButtonConnectActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        jFileChooser1.showSaveDialog(this);
        local_connection.
                send_file(jFileChooser1.getSelectedFile());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextLocIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextLocIPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextLocIPActionPerformed

    
    /**
     * Update the value of the connection object and set on the Connect button
     * @param th - new connection object
     */
    public void set_local_connection(Connection_tcp th) {
        local_connection= th;
        jToggleButtonConnect.setSelected(th != null); // Set ON/OFF the "Connect" button
    }

    /**
     * Regist and activate a connection object, used to send and receive
     * messages
     *
     * @param s- socket object associated to a TCP connection
     * @return the created thread object
     */
    public Connection_tcp start_connection_thread(Socket s) {
        Connection_tcp c = new Connection_tcp(this, s); // Create the connection thread object
        Log_rem("Connected to " + c.toString() + "\n");
        c.start();                           // Start the connection thread
        connlist.put(c.toString(), c); // Add object c to the connections list
        return c;
    }
    
    /**
     * Callback called by the connection thread to signal the end of the
     * connection It restarts the thread used to accept new connections
     * @param th    thread associated to the connection
     */
    public void connection_thread_ended(Connection_tcp th) {
        Log_rem("Connection to " + th.toString() + " ended\n");
        set_local_connection(null);

        if (th == local_connection) { // if it is the thread initiated locally
            set_local_connection(null);
        }
        connlist.remove(th.toString()); // Removes the thread from the list using the key
        // Restart the Daemon_tcp thread to wait for a new connection
        //serv_tcp = new Daemon_tcp(this, ss);  // Create the receiver thread
        //serv_tcp.start();                   // Start the receiver thread
    }

    /**
     * Send a packet to the remote IP:port with the message
     */
    public synchronized void send_packet() {
        
        String message = jTextMessage.getText();/*
        if(!connlist.isEmpty()){return;}
        
        if (local_connection == null) {
                Log_loc("Connection isn't active!\n");
            }
            //String message = jTextMessage.getText();    // Get the text from the Message box
            if (message.length() == 0) {
                Log_loc("Empty message: not sent\n");
                return;
            }
            if (local_connection.send_message(message)) {   // Send the message using the local_connection object
                // Write message to jTextAreaLocal
                Log_loc(formatter.format(new Date()) + " - sent to "
                        + local_connection.toString() + " - '" + message + "'\n");
        */
        for (Connection_tcp c: connlist.values()) {
            
            if (c == null) {
                Log_loc("Connection isn't active!\n");
            }
                        
            if (message.length() == 0) {
                Log_loc("Empty message: not sent\n");
                return;
            }
            if (c.send_message(message)) {   // Send the message using the local_connection object
                // Write message to jTextAreaLocal
                Log_loc(formatter.format(new Date()) + " - sent to "
                        + c.toString() + " - '" + message + "'\n");
            }
        }
    }
    

    /**
     * Handles an incoming message from a connection
     * @param con   thread associated to the connection
     * @param msg   message received
     */
    public synchronized void receive_message(Connection_tcp con, String msg) {
        try {
            // Get reception date
            Date date = new Date();

            // Write message contents
            Log_rem(formatter.format(date) + " - received from "
                    + con.toString() + " - '" + msg + "'\n");
        } catch (Exception e) {
            Log_rem("Error in receive_message: " + e + "\n");
        }
    }

    /**
     * Outputs a message to the "Local" text area
     * @param s message to be echoed
     */
    public synchronized void Log_loc(String s) {
        try {
            jTextAreaLocal.append(s);
            System.out.print("Local: " + s);
        } catch (Exception e) {
            System.err.println("Error in Log_loc: " + e + "\n");
        }
    }

    /**
     * Outputs a message to the "Remote" text area
     * @param s message to be echoed
     */
    public synchronized void Log_rem(String s) {
        try {
            jTextAreaRemote.append(s);
            System.out.print("Remote: " + s);
        } catch (Exception e) {
            System.err.println("Error in Log_rem: " + e + "\n");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Chat_tcp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Chat_tcp().setVisible(true);
        });
    }
    
    // Variables declaration
    private ServerSocket ss;     // Server socket - to receive connections
    private Daemon_tcp serv_tcp;     // Thread for connection reception
    private Connection_tcp local_connection;    // Connection object
    private final java.text.SimpleDateFormat formatter = // Formatter for dates
            new java.text.SimpleDateFormat("hh:mm:ss");
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonSend;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelLocal;
    private javax.swing.JLabel jLabelRemote;
    private javax.swing.JPanel jPanelLocal;
    private javax.swing.JPanel jPanelMessage;
    private javax.swing.JPanel jPanelRemote;
    private javax.swing.JScrollPane jScrollPaneLocal;
    private javax.swing.JScrollPane jScrollPaneRemote;
    private javax.swing.JTextArea jTextAreaLocal;
    private javax.swing.JTextArea jTextAreaRemote;
    private javax.swing.JTextField jTextLocIP;
    private javax.swing.JTextField jTextLocPort;
    private javax.swing.JTextField jTextMessage;
    private javax.swing.JTextField jTextRemIP;
    private javax.swing.JTextField jTextRemPort;
    private javax.swing.JToggleButton jToggleButtonActive;
    private javax.swing.JToggleButton jToggleButtonConnect;
    // End of variables declaration//GEN-END:variables
}