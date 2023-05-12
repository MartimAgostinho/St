/*
 * Sistemas de Telecomunicacoes 
 *          2022/2023
 */
package simulator;

import java.util.StringTokenizer;


/**
 * Defines the frames exchanged between the terminal protocol objects and the 
 * channel. Implements methods to create new objects, to serialize and deserialize.
 * 
 * @author lflb@fct.unl.pt
 */
public class Frame implements DataFrameIF, AckFrameIF, NakFrameIF {
    
    /**
     * Undefined (uninitialized event)
     */
    public static final int UNDEFINED_FRAME = 0;
    
    /**
     * Data frame
     */
    public static final int DATA_FRAME = 21;
    
    /**
     * ACK frame
     */
    public static final int ACK_FRAME = 22;
    
    /**
     * NAK frame
     */
    public static final int NAK_FRAME = 23;
    
    /**
     * Undefined sequence number; it must be 0 or above
     */
    public static final int UNDEFINED_SEQ = -1;

    /**
     * Undefined buffer size. The receiving buffer size is unknown
     */
    public static final int UNDEFINED_BUF_SIZE = -1;
    
    /**
     * Maximum length of the selective ACK vector - not used
     */
    public static final int MAX_ACKVEC_LENGTH = 32;
    
    /**
     * Maximum length of the data information string
     */
    public static final int MAX_INFO_LENGTH = 100;

    /**
     * Constructor
     */
    public Frame() {
        kind = UNDEFINED_FRAME;
        info = null;
        seq = UNDEFINED_SEQ;
        ack = UNDEFINED_SEQ;
        rcvbufsize= UNDEFINED_BUF_SIZE;
        sendTime = Event.UNDEF_TIME;
        recvTime = Event.UNDEF_TIME;
    }

    /**
     * Resets the frame contents to UNDEFINED_FRAME
     */
    private void reset_frame() {
        kind = UNDEFINED_FRAME;
        info = null;
        seq = UNDEFINED_SEQ;
        ack = UNDEFINED_SEQ;
        rcvbufsize= UNDEFINED_BUF_SIZE;
        sendTime = Event.UNDEF_TIME;
        recvTime = Event.UNDEF_TIME;
    }

    
    /* Static methods to create new frame object instances */
    
    /**
     * Creates a new instance (object) of a Data frame 
     * @param seq sequence number
     * @param ack acknowledgment number
     * @param rcvbufsize receiving buffer size
     * @param info packet transmitted
     * @return the frame object created
     */
    public static Frame new_Data_Frame(int seq, int ack, int rcvbufsize, String info) {
        Frame frame= new Frame();
        frame.set_DATA_frame(seq, ack, rcvbufsize, info);
        return frame;
    }
    
    /**
     * Creates a new instance (object) of an Ack frame
     * @param ack acknowledgment number
     * @param rcvbufsize receiver buffer size
     * @return the frame object created
     */
    public static Frame new_Ack_Frame(int ack, int rcvbufsize) {
        Frame frame= new Frame();
        frame.set_ACK_frame(ack, rcvbufsize);
        return frame;
    }

    /**
     * Creates a new instance (object) of an Nak frame
     * @param nak acknowledgment number
     * @param rcvbufsize receiver buffer size
     * @return the frame object created
     **/
    public static Frame new_Nak_Frame(int nak, int rcvbufsize) {
        Frame frame= new Frame();
        frame.set_NAK_frame(nak, rcvbufsize);
        return frame;
    }

    /* Methods to read the frame fields' contents */
    /* They should be used through variables of Interface classes DataFrameIF, AckFrameIF and NakFrameIF */ 
        
    /**
     * Get the kind of the frame object
     * @return the kind of the frame
     */
    @Override
    public int kind() {
        return kind;
    }
    
    /**
     * Get the sequence number (valid for DATA_FRAME kind)
     * @return the sequence number
     */
    @Override
    public int seq() {
        return seq;
    }

    /**
     * Get the acknowledgement number
     * @return the acknowledgement number
     */
    @Override
    public int ack() {
        return ack;
    }
    
    /**
     * Get the negative acknowledgement number
     * @return the acknowledgement number
     */
    @Override
    public int nak() {
        return ack; // ack field is used to carry the NAK value for a NAK frame
    }
    /**
     * Get the receiving buffer size
     * @return the buffer size
     */
    @Override
    public int rcvbufsize() {
        return rcvbufsize;
    }

    /**
     * Get the information of a DATA_FRAME
     * @return the information carried
     */
    @Override
    public String info() {
        return info;
    }

    /**
     * Get the initial sending time of the frame
     * @return the sending time, or Event.UNDEF_TIME
     */
    public long snd_time() {
        return this.sendTime;
    }

    /**
     * Get the reception time of the frame
     * @return the reception time, or Event.UNDEF_TIME
     */
    public long rcv_time() {
        return this.recvTime;
    }

    /**
     * Test if the event is undefined
     * @return true if is undefined, false otherwise
     */
    boolean is_undef() {
        return kind == UNDEFINED_FRAME;
    }
    
    /**
     * Test if the frames contents are equal, ignoring times 
     * @param f other frame
     * @return true if equal
     */
    public boolean equal_content(Frame f) {
        if ((f.kind != this.kind) || (this.kind == Frame.UNDEFINED_FRAME))
            return false;
        switch (this.kind()) {
            case Frame.DATA_FRAME:
                if (f.seq != this.seq)
                    return false;
                if (f.info==null || this.info==null || !f.info.equals(this.info))
                    return false;
            case Frame.ACK_FRAME:
            case Frame.NAK_FRAME:
                if (f.ack != this.ack)
                    return false;
                if (f.rcvbufsize != this.rcvbufsize)
                    return false;
                return true;
                
            default:
                return false;
        }
    }

    /**
     * Get a string with the kind of the frame
     * @return string with the kind of frame
     */
    public String kindString() {
        String str;
        switch (kind) {
            case UNDEFINED_FRAME:
                str = "UNDEFINED";
                break;
            case DATA_FRAME:
                str = "DATA";
                break;
            case ACK_FRAME:
                str = "ACK";
                break;
            case NAK_FRAME:
                str = "NAK";
                break;
            default:
                str = "INVALID";
        }
        return str;
    }

    /**
     * Returns a string with the frame object's contents
     * @return string with the frame object's contents
     */
    @Override
    public String toString() {
        String str = kindString();
        if (kind == DATA_FRAME) {
            str += " " + (seq == UNDEFINED_SEQ ? "undef seq" : seq);
        }
        if (kind == DATA_FRAME || kind == ACK_FRAME || kind == NAK_FRAME) {
            str += " " + (ack == UNDEFINED_SEQ ? "undef ack" : ack);
            str += " " + (rcvbufsize == UNDEFINED_BUF_SIZE ? "undef rbuf" : rcvbufsize);
        }
        // ...
        return str;
    }

    /**
     * Used with DATA_FRAME frames to set the fields values
     * @param seq sequence number
     * @param ack acknowledgement number
     * @param rcvbufsize receiving buffer size
     * @param info packet information
     * @return true if successful, false otherwise
     */
    private boolean set_DATA_frame(int seq, int ack, int rcvbufsize, String info) {
        if ((seq <= UNDEFINED_SEQ) || (ack <= UNDEFINED_SEQ) || (info == null)
                || info.isEmpty() || (info.length() > MAX_INFO_LENGTH)) {
            return false;
        }
        kind = DATA_FRAME;
        this.seq = seq;
        this.ack = ack;
        this.rcvbufsize= rcvbufsize;
        this.info = info;
        return true;
    }

    /**
     * Used with ACK_FRAME frames to set the fields values
     * @param ack acknowledgement number
     * @param rcvbufsize receiving buffer size
     * @return true if successful, false otherwise
     */
    private boolean set_ACK_frame(int ack, int rcvbufsize) {
        if (ack <= UNDEFINED_SEQ) {
            return false;
        }
        kind = ACK_FRAME;
        this.ack = ack;
        this.rcvbufsize= rcvbufsize;
        return true;
    }

    /**
     * Used with NAK_FRAME frames to set the fields values
     * @param nak missing sequence number
     * @param rcvbufsize receiving buffer size
     * @return true if successful, false otherwise
     */
    private boolean set_NAK_frame(int nak, int rcvbufsize) {
        if (nak <= UNDEFINED_SEQ) {
            return false;
        }
        kind = NAK_FRAME;
        this.ack = nak;
        this.rcvbufsize= rcvbufsize;
        return true;
    }

    /**
     * Set the frame sending time
     * @param sendTime initial sending time
     * @return true if successful, false otherwise
     */
    public boolean set_sendTime(long sendTime) {
        if ((sendTime != Event.UNDEF_TIME) && (sendTime < 0)) {
            return false;
        }
        this.sendTime = sendTime;
        return true;
    }

    /**
     * Set the frame reception time
     * @param recvTime reception time
     * @return true if successful, false otherwise
     */
    public boolean set_recvTime(long recvTime) {
        if ((recvTime != Event.UNDEF_TIME) && (recvTime < 0) || (sendTime >= recvTime)) {
            return false;
        }
        this.recvTime = recvTime;
        return true;
    }

    
    /**
     * Prepares a string with the frame contents, serializing the object
     * @return the string, or null if error
     */
    public String frame_to_str() {
        if (kind == UNDEFINED_FRAME) {
            return null;
        }
        String str = "";

        // Set initial header 
        switch (kind) {
            case DATA_FRAME:
                str = "DATA " + seq + " " + ack + " " + rcvbufsize;
                break;
            case ACK_FRAME:
                str = "ACK " + ack + " " + rcvbufsize;
                break;
            case NAK_FRAME:
                str = "NAK " + ack + " " + rcvbufsize;
                break;
        }

        // Write sendTime
        if (sendTime != Event.UNDEF_TIME) {
            str += " SNDTIME " + sendTime;
        }

        // Write sendTime
        if (recvTime != Event.UNDEF_TIME) {
            str += " RCVTIME " + recvTime;
        }

        // Write DATA
        if (info != null) {
            str += " INFO " + info.length() + " " + info;
        }

        return str;
    }

    /**
     * Decodes the contents of a string to the frame, desirealizing it
     * @param line - string with the frame's contents
     * @param log - Logging object
     * @return true if decoded successfully, false otherwise
     */
    public boolean str_to_frame(String line, Log log) {
        if (line == null) {
            return false;
        }
        StringTokenizer st = new StringTokenizer(line);
        String cmd = null;

        try {
            while (st.hasMoreTokens()) {
                cmd = st.nextToken();
                switch (cmd) {
                    case "DATA":
                        if (kind != UNDEFINED_FRAME) {
                            log.Log("Can have only one DATA,ACK or NAK\n");
                            reset_frame();
                            return false;
                        }   
                        kind = DATA_FRAME;
                        if (st.countTokens() < 3) {
                            log.Log("Received DATA without enough parameters\n");
                            reset_frame();
                            return false;
                        }   
                        seq = Event.parseInt(st.nextToken());
                        ack = Event.parseInt(st.nextToken());
                        rcvbufsize = Event.parseInt(st.nextToken());
                        break;
                    case "ACK":
                        if (kind != UNDEFINED_FRAME) {
                            log.Log("Can have only one DATA,ACK or NAK\n");
                            reset_frame();
                            return false;
                        }   
                        kind = ACK_FRAME;
                        if (st.countTokens() < 2) {
                            log.Log("Received ACK without enough parameters\n");
                            reset_frame();
                            return false;
                        }   
                        ack = Event.parseInt(st.nextToken());
                        rcvbufsize = Event.parseInt(st.nextToken());
                        break;
                    case "NAK":
                        if (kind != UNDEFINED_FRAME) {
                            log.Log("Can have only one DATA,ACK or NAK\n");
                            reset_frame();
                            return false;
                        }   
                        kind = NAK_FRAME;
                        if (st.countTokens() < 2) {
                            log.Log("Received NAK without enough parameters\n");
                            reset_frame();
                            return false;
                        }   
                        ack = Event.parseInt(st.nextToken());
                        rcvbufsize = Event.parseInt(st.nextToken());
                        break;
                    case "SNDTIME":
                        if (st.countTokens() < 1) {
                            log.Log("Received SNDTIME without enough parameters\n");
                            reset_frame();
                            return false;
                        }   
                        sendTime = Event.parseLong(st.nextToken());
                        break;
                    case "RCVTIME":
                        if (st.countTokens() < 1) {
                            log.Log("Received RCVTIME without enough parameters\n");
                            reset_frame();
                            return false;
                        }   
                        recvTime = Event.parseLong(st.nextToken());
                        break;
                    case "INFO":
                        if (st.hasMoreTokens()) {
                            int len = Event.parseInt(st.nextToken());
                            if (len < 1 || len > MAX_INFO_LENGTH) {
                                log.Log("Received DATA with invalid data length\n");
                                reset_frame();
                                return false;
                            }
                            info = st.nextToken();
                            if (info.length() != len) {
                                log.Log("Received DATA with invalid length (" + len + "!=" + info.length() + ")\n");
                                reset_frame();
                                return false;
                            }
                        } else {
                            log.Log("Received INFO without enough parameters\n");
                            reset_frame();
                            return false;
                        }   
                        break;
                    default:
                        log.Log("Received invalid token '" + cmd + "'\n");
                        reset_frame();
                        return false;
                }
            }
            return true;
        } catch (NumberFormatException ne) {
            log.Log("Invalid number in " + (cmd == null ? "" : cmd) + " element\n");
            reset_frame();
            return false;
        } catch (Exception e) {
            log.Log("Exception in " + (cmd == null ? "" : cmd) + " element: " + e + "\n");
            reset_frame();
            return false;
        }
    }
    
    
    /* Frame fields */
    
    /**
     * Frame kind - can be: UNDEFINED_PCKT or DATA_PCKT or ACK_PCKT
     */
    private int kind;
    
    /**
     * Data - only used for DATA packets
     */
    private String info;
    
    /**
     * Sequence number - only used for DATA packets
     */
    private int seq;
    
    /**
     * Acknowledge number - for DATA, ACK and NAK packets; it defines the 
     * sequence number of the last DATA frame successfully received for DATA and 
     * ACK; if defines the missing DATA frame for NAK packets
     */
    private int ack;
    
    /**
     * Receiver buffer size - for DATA, ACK and NAK packets; it defines the 
     * space available in the receiving buffer. 
     * If size is unknown, it is equal to UNDEFINED_BUF_SIZE
     */
    private int rcvbufsize;
    
    /**
     * Frame sending time
     */
    private long sendTime;
    
    /**
     * Frame reception time
     */
    private long recvTime;
}
