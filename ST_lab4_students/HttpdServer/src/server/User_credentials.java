/**
 * Sistemas de Telecomunicacoes 
 *          2022/2023
 *
 * User_credentials.java
 *
 * Class that stores and manages the challenge-response information
 *
 * Created on March 20, 2022
 *
 * @author Luis Bernardo
 */
package server;

import java.util.Date;

/**
 * Manage challenge-response information
 */
public class User_credentials {
    /**
     * Challenge-Response vector length
     */
    public static final int CHALLENGE_LENGTH= 3;
    /**
     * User name
     */
    private final String user;
    /**
     * Challenge: positions of the password requested
     */
    private final int[] positions;
    /**
     * Answer to the challenge - letters of the password in positions
     */
    private final String key;
    /**
     * Creation date
     */
    private final Date creation_date;
    /**
     * Validity of the challenge-response data
     */
    private final long Lifetime;

    /**
     * Constructor - stores the object fields and the creation date
     * @param user
     * @param pos
     * @param key
     * @param validity 
     */
    public User_credentials(String user, int[] pos, String key, long validity) {
        this.user= user;
        positions= pos;
        this.key= key;
        creation_date= new Date();
        Lifetime= validity;
    }

    /**
     * Return the user name
     * @return the user name
     */
    public String get_user() {
        return user;
    }
    /**
     * Returns the character positions in the password to be returned by the user
     * @return the array with the positions
     */
    public int[] get_challenge() {
        return positions;
    }

    /**
     * Checks if the entry is is_valid (within its lifetime)
     * @return true if is_valid, false otherwise
     */
    public boolean is_valid() {
        return ((creation_date.getTime()+Lifetime)>System.currentTimeMillis());
    }
    
    /**
     * Check if a valid challenge-response is stored
     * @return true if has challenge-response
     */
    public boolean has_challenge() {
        return is_valid() && (key!=null) && (key.length()==CHALLENGE_LENGTH);
    }
    
    /**
     * Validates the string sent by the browser
     * @param str   string with client string
     * @return true if is_valid, else otherwise
     */
    public boolean validate_client_string(String str) {
        // Example of a string - user=user&P1=1&K1=a&P2=2&K2=b&P3=3&K3=d&Login=Login
        String check_key= toString();
        
        if (str != null)
            return str.startsWith(check_key);   // Ignore the last part ("&Login=Login")
        else {
            System.err.println("Error is software: null pointer in ChallengeResponse.validate_client_string");
            return false;
        }
    }
    
    /**
     * Converts the contents to string
     * @return string with the challenge-response information
     */
    @Override
    public String toString() {
        String str= "user="+user;
        for (int i=0; i<CHALLENGE_LENGTH; i++) {
            str += "&P"+(i+1)+"=";
            if ((positions!=null) && (key!=null) && (key.length()==CHALLENGE_LENGTH)) {
                str += positions[i];
            }
            str += "&K"+(i+1)+"=";
            if ((positions!=null) && (key!=null) && (key.length()==CHALLENGE_LENGTH)) {
                str += key.charAt(i);
            }
        }
        return str;
    }
  
}