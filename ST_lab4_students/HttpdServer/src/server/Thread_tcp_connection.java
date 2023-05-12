/**
 * Sistemas de Telecomunicacoes
 *          2022/2023
 *
 * Thread_tcp_connection.java
 *
 * Thread class that handles the (over)simplified HTTP protocol message exchange
 *
 * Created on March 20, 2022
 *
 * @author Luis Bernardo
 */
package server;

import java.io.*;
import static java.lang.System.exit;
import java.net.*;
import java.util.*;

public class Thread_tcp_connection extends Thread {

    /**
     * Reference to the GUI object
     */
    Web_server_main_thread main_thread;
    /**
     * Connection socket, to communicate with the browser
     */
    Socket soc_to_browser;
    /**
     * Object PrintStream to write easily into socket
     */
    PrintStream print_out;
    /**
     * Object BufferedReadwer to read easily from socket
     */
    BufferedReader read_in;

    /**
     * Constructor - creates a new instance of Thread_tcp_connection
     *
     * @param main_thread GUI object
     * @param client Connections socket
     */
    public Thread_tcp_connection(Web_server_main_thread main_thread, Socket client) {
        this.main_thread = main_thread;
        soc_to_browser = client;
        try {
            read_in = new BufferedReader(
                    new InputStreamReader(soc_to_browser.getInputStream(), "8859_1"));
            
            OutputStream out = this.soc_to_browser.getOutputStream();
            print_out = new PrintStream(out, false, "8859_1");
        } catch (IOException e) {
            System.out.println("I/O error in establishing the PrintStream object" + e);
            exit(1);
        }
    }

    /**
     * ****************************************************************************\
     * Methods that prepare custom html files with webpages
     * \*****************************************************************************
     */
    /**
     * Prepares the login1CGI initial login html page to receive the username
     *
     * @param ip IP of the browser
     * @param port port of the browser
     * @param user user ID
     * @param message Message to echo in the browser
     * @return String with the content of the web page
     */
    public String make_login1Page(String ip, int port, String user, String message) {

        // Prepare string html with web page
        String html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<html>\r\n<head>\r\n";
        html += "<meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"content-type\">\r\n";
        html += "<title>STlogin.htm</title>\r\n</head>\r\n<body>\r\n";
        html += "<h1 align=\"center\">Authentication Page 1 for ST web server</h1>\r\n";
        html += "<h1 align=\"center\"><font color=\"#800000\">Sistemas de Telecomunica&ccedil;&otilde;es</font> <font color=\"#c0c0c0\">2022/2023</font></h1>\r\n";
        html += "<h3 align=\"center\">4th Lab work</h3>\r\n";
        html += "<p align=\"left\">Connection received from <font color=\"#ff0000\">" + ip + "</font>:";
        html += "<font color=\"#ff0000\">" + port + "</font>.</p>\r\n";

        if (message != null && message.length() > 0) {
            html += "<font color=\"#0000ff\">" + message + "</font>\r\n";
        }
        html += "<form method=\"post\" action=\"STlogin1.cgi\">\r\n<h3>\r\nUser login</h3>";
        html += "<p>Username <input name=\"user\" size=\"10\" type=\"text\""
                + (((user != null) && (user.length() > 0)) ? " value=\"" + user + "\"" : "") + "></p>\r\n";
        html += "<p><input value=\"Login\" name=\"Login\" type=\"submit\"><input value=\"Clear\" type=\"reset\" value=\"Reset\" name=\"Reset\"></p>\r\n</form>\r\n";
        html += "</body>\r\n</html>\r\n";

        return html; // string with HTML page code
    }

    /**
     * Prepares the login2CGI web page to present the challenge and receive the
     * answer
     *
     * @param ip IP of the browser
     * @param port port of the browser
     * @param user user ID
     * @param challenge vector with the positions to request
     * @param message Message to echo in the browser
     * @return String with the content of the web page
     */
    public String make_login2Page(String ip, int port, String user, int[] challenge, String message) {

        // Prepare string html with web page
        String html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<html>\r\n<head>\r\n";
        html += "<meta content=\"text/html; charset=ISO-8859-1\" http-equiv=\"content-type\">\r\n";
        html += "<title>STlogin.htm</title>\r\n</head>\r\n<body>\r\n";
        html += "<h1 align=\"center\">Authentication Page 2 for ST web server</h1>\r\n";
        html += "<h1 align=\"center\"><font color=\"#800000\">Sistemas de Telecomunica&ccedil;&otilde;es</font> <font color=\"#c0c0c0\">2022/2023</font></h1>\r\n";
        html += "<h3 align=\"center\">4th Lab work</h3>\r\n";
        html += "<p align=\"left\">Connection received from <font color=\"#ff0000\">" + ip + "</font>:";
        html += "<font color=\"#ff0000\">" + port + "</font>.</p>\r\n";

        if (message != null && message.length() > 0) {
            html += "<font color=\"#0000ff\">" + message + "</font>\r\n";
        }
        html += "<form method=\"post\" action=\"STlogin2.cgi\">\r\n<h3>\r\nUser login</h3>";
        html += "<p>Username <input name=\"user\" size=\"10\" type=\"text\""
                + (((user != null) && (user.length() > 0)) ? " value=\"" + user + "\"" : "") + "></p>\r\n";
        if (challenge != null) {
            for (int i = 0; i < challenge.length; i++) {
                html += "<p>Position <input name=\"P" + (i + 1) + "\" size=\"2\" type=\"text\" value="
                        + challenge[i] + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Key <input name=\"K"
                        + (i + 1) + "\" size=\"1\" type=\"text\"></p>\r\n";
            }
        }
        html += "<p><input value=\"Login\" name=\"Login\" type=\"submit\"><input value=\"Clear\" type=\"reset\" value=\"Reset\" name=\"Reset\"></p>\r\n</form>\r\n";
        html += "</body>\r\n</html>\r\n";

        return html; // HTML page code
    }

    /**
     * ****************************************************************************\
     * Methods that return to the browser a webpage or error messages
     * \*****************************************************************************
     */
    /**
     * Sends a "404 Not Found" error message to the brower, with a sample HTML
     * web page
     *
     * @param pout Stream object associated to the browser's socket
     */
    private void return_error_to_browser() {
        print_out.print("HTTP/1.0 404 Not Found\r\nServer: " + Web_server_main_thread.SERVER_NAME + "\r\n\r\n");
        // Prepares a web page with an error description
        print_out.print("<HTML>\r\n");
        print_out.print("<HEAD><TITLE>Not Found</TITLE></HEAD>\r\n");
        print_out.print("<H1> Page not found </H1>\r\n");
        print_out.print("</HTML>\r\n");
    }

    /**
     * Sends the content of a file to the browser, or error 404 if file does not
     * exist
     *
     * @param filename File to be transmitted
     * @param set_cookie_value value of the set-cookie parameter, or null if not
     * transmitted
     */
    private void return_file_content_to_browser(String filename, String set_cookie_value) {
        try {
            String pathname = main_thread.getRootHtmlDirectory() + filename + (filename.equals("/") ? Web_server_main_thread.HOMEFILENAME : "");
            System.out.println("Filename= '" + pathname + "'");
            File f = new File(pathname);
            if (!f.exists() || !f.isFile() || !f.canRead()) {
                // File does not exist or cannot read
                System.out.println("Filename= " + pathname + " does not exist or cannot read");
                return_error_to_browser();
                return;
            }

            FileInputStream fis = new FileInputStream(f);
            byte[] data = new byte[fis.available()]; // Alocate an array with the size of the filename 
            fis.read(data); // Reads the entire filename to memory
            fis.close(); // Closes the filename 

            // Write the HTTP "200 OK" header
            print_out.print("HTTP/1.0 200 OK\r\nServer: " + Web_server_main_thread.SERVER_NAME + "\r\n");

            if (set_cookie_value != null) {
                /**
                 * ******************************
                 * TASK 6.3.
                 * Send a Set-Cookie header field to the browser with the content
                 * of the string set_cookie_value, if this is not null.
                 */
                // ...
                main_thread.Log("return_file_content_to_browser is not sending Set-Cookie\n");
            }
            print_out.print("\r\n");
            print_out.write(data); // Writes the filename contents to the socket
            print_out.flush(); // Forces the sending

        } catch (FileNotFoundException e) {
            // Handle unpredicted filename errors
            System.out.println("File not found");
            // Writes a "filename not found" error to the socket
            return_error_to_browser();
        } catch (IOException e) {
            System.out.println("I/O error " + e);
            // Writes a "filename not found" error to the socket
            return_error_to_browser();
        }
    }

    /**
     * Sends the content of a string to the brower
     *
     * @param html String with html to be transmitted
     */
    private void return_string_content_to_browser(String html) {
        if (html == null) {
            return;
        }
        /**
         * ******************************
         * TASK 6.1: Send 
         *     a) the HTTP reply headers
         *     b) the line \r\n
         *     c) the content of html variable to the browser.
         */
        
        print_out.print("HTTP/1.0 200 OK\r\nServer: " + Web_server_main_thread.SERVER_NAME + "\r\n");
        print_out.print("\r\n");
        print_out.print(html); // Writes the filename contents to the socket
        print_out.flush(); // Forces the sending
        
    }

    /**
     * ****************************************************************************\
     * Auxiliary methods to read and store information from messages received
     * from the browser
     * \*****************************************************************************
     */
    /**
     * Reads the lines with properties that composes the header of the request
     * If it is a GET it might appear the "Cookie", if it is a POST it appears the
     * "Content-Length" and the answers.
     * Returns the object with the data read.
     *
     * @param isPost If it is a POST request
     * @return GET_or_POST_answers object with the information received
     * @throws java.io.IOException
     */
    private GET_or_POST_answers read_cookie_from_GET_or_answer_from_POST (boolean isPost) throws IOException {
        GET_or_POST_answers info = new GET_or_POST_answers();
        String req;
        int len_answer = -1, ix, n, cnt = 0;

        // Reads all lines until \r\n (this line has 0 bytes)
        // In the meanwhile, if it is a GET it might have "Cookie" (see GET packet structure in the work document)
        //                   if it is a POST it has "Content-Length" (see POST packet structure in the work document)
        while (((req = read_in.readLine()) != null) && (req.length() != 0)) {
            System.out.println("hdr(" + req + ")");
            if (req.startsWith("Content-Length: ")) {
                req = req.substring(16).trim();
                try {
                    len_answer = Integer.parseInt(req);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            } else if (req.contains("Cookie")) {
                if ((ix = req.indexOf(':')) != -1) {  // Valid structure
                    info.set_cookie(req.substring(ix + 1).trim());
                }
            }
        }

        // If it is a POST, reads answer after the header
        // The length of the answer is len_answer
        if (isPost && read_in.ready() && (len_answer > 0)) {
            char[] cbuf = new char[len_answer];
            String str = new String();

            System.out.println("Get POST parameters (len=" + len_answer + ")");
            while ((cnt < len_answer) && (n = read_in.read(cbuf)) > 0) {
                str = str + new String(cbuf);
                cnt += n;
            }
            info.set_POST_answer(str);
        }

        return info;
    }

    /**
     * Returns the username contained on a Cookie or Form parameters string
     *
     * @param key string received on a cookie or form parameters string
     * @return the username, or null if not present
     */
    private String get_user_from_cookie_or_answer(String key) {
        if (key == null) {
            main_thread.Log("ERROR: get_user_from_cookie called with null key\n");
            return null;
        }
        int userbegin = key.indexOf("user="), userend = key.indexOf('&');
        if ((userbegin >= 0) && (userend > 0)) {
            return key.substring(userbegin + 5, userend);
        } else {
            return null;
        }
    }

    /**
     * Validate if a key received on a Cookie or Form is valid
     *
     * @param key string received
     * @return true if it is valid
     */
    public boolean valid_KEY(String key) {
        if (key == null) {
            main_thread.Log("ERROR: valid_KEY called with null key\n");
            return false;
        }
        main_thread.Log("valid_KEY(" + key + ") not implemented yet\n");

        /**
         * ******************************
         * TASK 7.5 Complete this function to validate the "key" received from
         * Form 2 or a cookie.
         * Suggestions: You need the key to get the username, and the username
         * to get a User_credentials object in cache or from the authentication
         * server. Finally, use User_credentials's method validate_client_string
         * to check if the key is a valid reply to the challenge.
         */
        String user = get_user_from_cookie_or_answer(key);
        if (user == null) {
            main_thread.Log("Error: invalid user in valid_KEY parameter\n");
            return false;
        }
        // ...

        return true;
    }

    /**
     * ****************************************************************************\
     * Thread_tcp_connection code
     * \*****************************************************************************
     */
    /**
     * Thread code that waits for a request and send the answer
     */
    @Override
    public void run() {
        try {
            String request = read_in.readLine(); // Read the first line 
            if (request == null || request.isEmpty()) // End of connection
            {
                return;
            }

            // The first request line has the format "GET /file.html HTTP/1.1"
            // This code uses the "StringTokenizer" class to separate the 3 components
            main_thread.Log("\nRequest= '" + request + '\n');
            StringTokenizer st = new StringTokenizer(request);
            if (st.countTokens() != 3) // If it does not have the 3 components isn't is_valid
            {
                return; // Invalid request
            }
            String method = st.nextToken(); // It has to be a "GET" here
            String file = st.nextToken();   // Get the second token and ignore the third one

            /**
             * ******************************
             * TASK 6.2. First phase of the web server. This task includes the
             * following actions: 
             *   a) Try to read the HTTP headers (Cookie, Content-Length, answers)
             *   b) If it is a GET with a cookie, or a POST with answers, get
             *      the username and validate it using the get_challengeresponse
             *      method 
             *   c) If the user is valid, send the page requested, or "/", to the 
             *      browser
             *   d) If the user is not valid, or was not sent, send the Form 1 to 
             *      the browser
             */
            
            
            
            /**
             * ******************************
             * TASK 6.2
             *   a) Try to read the HTTP headers (Cookie, Content-Length, answers)
             * Suggestion: Use the function read_cookie_from_GET_or_answer_from_POST(...)
             *             to read the fields and return a GET_or_POST_answers object
             */
            if( !method.equals("GET") && !method.equals("POST") ){ return; }
            GET_or_POST_answers answers = read_cookie_from_GET_or_answer_from_POST(method.equals("POST"));

            //Ha cookie e ]e GET
            if (answers.get_cookie() != null && method.equals("GET") ) {
                
                //main_thread.get_challengeresponse_from_server( answer. );
                //.get_challengeresponse();
                //validar cookie mandar site
                
                //credenciais do servidor
                User_credentials creds = main_thread.get_challengeresponse(
                                                    get_user_from_cookie_or_answer(
                                                                    answers.get_cookie() ));
                if( creds == null ){ return; }
                
                if( !creds.validate_client_string(answers.get_cookie() ) ){
                
                     return;   
                };
                
                
                
            }else if( answers.get_cookie() == null && method.equals("GET") ){
            
                //mandar forms
                
                String html= make_login1Page(soc_to_browser.getInetAddress().getHostAddress() /*IP*/,
                        soc_to_browser.getPort() /* port*/, null /*user*/,
                    "Enter your username" /* message */);
                return_string_content_to_browser(html);
                return;
            
            }else if( answers.get_cookie() == null && method.equals("POST") ){
                //SET COOKIE
                
                String S = "HTTP/1.0 200 OK\\r\\n\n" +
                            "Server: ST 2021/2022\\r\\n\n" +
                            "Set-Cookie: "+ answers.get_POST_answers() +"; path=/\\r\\n\n" +
                            "\\r\\n";
                
                file = "/";
                return_string_content_to_browser(S);

                

            }else{ /*ERRO*/ }
            
            //if ( answers.get_POST_answer() != null) {
            //    print_out.print("COKIEEEEEEEE\n");
            //}   
            
            /**
             * ******************************
             * TASK 6.2
             *    b) Test if it is a GET with a cookie, or a POST. Get the
             *       username and validate it using the get_challengeresponse method
             * Suggestions: Use the function get_user_from_cookie(...) to get the
             *              user name from the cookie or from the POST answer.
             *    If the user is not valid use function make_login1Page to create
             *    form 1 and function return_string_content_to_browser to send form 1
             *    to the browser.
             */

            /**
             * ******************************
             * TASK 7.4.
             * If a valid user is received after Form 1 and a challenge-response
             * pair is received from the authentication server, the web server
             * should send the Form 2 to the browser with the challenge received.
             * Suggestions: Use function make_login2Page to create form 2 and
             *              function return_string_content_to_browser to send
             *              form 2 to the browser.
             */
            // Return the file contents to the browser
            /**
             * ******************************
             * TASK 7.6.
             * Modify your code to only send the web page contents
             * when a valid key is received in a POST to form 2 or in a cookie.
             */
            // Return the file contents to the browser
            /**
             * ******************************
             * TASK 6.4.
             * Add a Set-Cookie string when a file is transmitted to the browser
             */
            return_file_content_to_browser(file, null);

        } catch (IOException e) {
            System.out.println("I/O error " + e);
        } catch (Exception e) {
            System.out.println("Error " + e);
        } finally {
            // This code is always run, even when there are exceptions
            try {
                soc_to_browser.close(); // Closes the socket and all associated streams
            } catch (Exception e) {
                /* Ignore everything */
            }
        }
    }
}
