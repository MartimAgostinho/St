/*
 * Sistemas de Telecomunicacoes 
 *          2022/2023
 */
package simulator;


/**
 * Defines the frames exchanged between the terminal protocol objects and the 
 * channel. Implements methods to read the frame fields' contents.
 * 
 * @author lflb@fct.unl.pt
 */
public interface NakFrameIF {
        
    /* Methods to read the frame fields' contents */
        
    /**
     * Get the kind of the frame object
     * @return the kind of the frame
     */
    public int kind();
    
    /**
     * Get the acknowledgement number
     * @return the acknowledgement number
     */
    public int nak();

    /**
     * Get the receiving buffer size
     * @return the buffer size
     */
    public int rcvbufsize();

}
