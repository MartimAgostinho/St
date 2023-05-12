/**
 * Sistemas de Telecomunicacoes 
 *          2022/2023
 *
 * GET_or_POST_answers.java
 *
 * Class that stores and manages the GET cookie or POST answers information
 *
 * Created on March 20, 2022
 *
 * @author Luis Bernardo
 */
package server;

/**
 * Class with relevant header information
 */
class GET_or_POST_answers {

    /**
     * Cookie contents received from the browser
     */
    private String cookie;
    /**
     * Data content of a POST
     */
    private String POST_answer;

    /**
     * Constructor of an empty request
     */
    public GET_or_POST_answers() {
        cookie = null;
        POST_answer = null;
    }

    public String get_cookie() {
        return cookie;
    }

    public String get_POST_answers() {
        return POST_answer;
    }
   

    /**
     * Set the cookie received
     *
     * @param cookie
     */
    public void set_cookie(String cookie) {
        this.cookie = cookie;
    }

    /**
     * Set the POST answer value
     * @param paramstr - POST data string
     */
    public void set_POST_answer (String paramstr) {
        this.POST_answer = paramstr;
    }
    
}
