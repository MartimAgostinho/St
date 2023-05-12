/*
 * Sistemas de Telecomunicacoes 
 *          2022/2023
 */
package simulator;

import java.util.StringTokenizer;

/**
 * Defines the events exchanged between the terminal protocol objects and the 
 * channel. Implements methods to create new objects, to serialize and deserialize.
 * 
 * @author lflb@fct.unl.pt
 */
public class Event {

    /**
     * Undefined (uninitialized event)
     */
    public static final int UNDEFINED_EVENT = 0;
    
    /**
     * Statistics Event - Event with statistics information
     */
    public static final int STAT_EVENT = 1;

    /**
     * Time update event
     */
    public static final int TIME_EVENT = 2;
    
    /**
     * Packet transmission event
     */
    public static final int FRAME_EVENT = 3;
    
    /**
     * Request/Cancel Set timer
     */
    public static final int START_TIMER = 4;
    
    /**
     * Timer expire event
     */
    public static final int TIMER_EVENT = 5;

    /**
     * End Event - End of a response to an event - for synchronizing events
     */
    public static final int END_EVENT = 6;    

    /**
     * End Event - End of a response to an event - for synchronizing events
     */
    public static final int DATA_END = 7;       

    /**
     * Packet transmission event
     */
    public static final int CANCEL_FRAME_EVENT = 8;

    /**
     * Start Event
     */
    public static final int START_EVENT = 9;
    
    /**
     * Stop event
     */
    public static final int STOP_EVENT = 10;
    
    /**
     * Request Config event
     */
    public static final int REQ_CONFIG = 11;
    
    /**
     * Configuration event
     */
    public static final int CONFIGURATION = 12;

    
    /**
     * Retransmitted data frame statistics key value
     */
    public static final int STAT_RETRANSMITED = 31;
    
    /**
     * Network payload transmitted statistics key value
     */
    public static final int STAT_PAYLOADS_TX = 32;
    
    /**
     * Network payload received statistics key value
     */
    public static final int STAT_PAYLOADS_RX = 33;
    
    /**
     * Invalid network payload received statistics key value
     */
    public static final int STAT_PAYLOADS_RX_INVALID = 34;
    
    /**
     * Network payload received with buffer full statistics key value
     */
    public static final int STAT_PAYLOADS_RX_BUFFERFULL = 35;
        

    /**
     * Undefined Simulation time
     */
    public static final long UNDEF_TIME = -1;

    /* Static methods to create new event object instances */
    
    /**
     * Creates a new instance of a START_TIMER event
     * @param log object that implements the Log function
     * @param time the current simulation time
     * @param key the timer's key
     * @param interval the timer's time interval
     * @return the event object
     */
    public static Event new_Start_Timer(Log log, long time, int key, long interval) {
        Event ev = new Event(log, START_TIMER, time);
        ev.interval = interval;
        ev.key= key;
        return ev;
    }

    /**
     * Creates a new instance of a STOP_EVENT event
     * @param log object that implements the Log function
     * @return the event object
     */
    public static Event new_Stop_Event(Log log) {
        Event ev = new Event(log, STOP_EVENT, 0);
        return ev;
    }
    
    /**
     * Creates a new instance of a END_EVENT event
     * @param log object that implements the Log function
     * @return the event object
     */
    public static Event new_End_Event(Log log) {
        Event ev = new Event(log, END_EVENT, 0);
        return ev;
    }
    
    /**
     * Creates a new instance of a FRAME_EVENT event
     * @param log object that implements the Log function
     * @param time the current simulation time
     * @param frame the frame that is sent with the event
     * @return the event object
     */
    public static Event new_Frame_Event(Log log, long time, Frame frame) {
        Event ev= new Event(log, FRAME_EVENT, time);
        ev.set_frame(frame);
        return ev;
    }

    /**
     * Creates a new instance of a CANCEL_FRAME_EVENT event
     * @param log object that implements the Log function
     * @param time the current simulation time
     * @param frame the frame that will be cancelled with the event
     * @return the event object
     */
    public static Event new_CancelFrame_Event(Log log, long time, Frame frame) {
        Event ev= new Event(log, CANCEL_FRAME_EVENT, time);
        ev.set_frame(frame);
        return ev;
    }

    /**
     * Creates a new instance of a STAT_TIMER event
     * @param log object that implements the Log function
     * @param time the current simulation time
     * @param key the statistical variable that should be incremented
     * @return the event object
     */
    public static Event new_Stat_Event(Log log, long time, int key) {
        Event ev= new Event(log, STAT_EVENT, time);
        ev.key= key;
        return ev;
    }
    
    /**
     * Creates a new instance of a CONFIGURATION event
     * @param log object that implements the Log function
     * @param time the current simulation time
     * @param protocol the protocol selected by the user
     * @param max_seq the maximum sequence number
     * @param swnd the sending window size
     * @param rwnd the receiving window size
     * @param timeout the data timeout value
     * @param packets the number of packets sent
     * @return the event object
     */
    public static Event new_Configuration_Event(Log log, long time, int protocol, 
            int max_seq, int swnd, int rwnd, long timeout, int packets) {
        Event ev= new Event(log, CONFIGURATION, time);
        ev.set_configuration(protocol, max_seq, swnd, rwnd, timeout, packets);
        return ev;
    }

    /**
     * Auxiliary static function to parse an integer from a string and handles null strings
     * @param str the string with the number
     * @return the integer value
     * @throws NumberFormatException if string is not valid
     */
    public static int parseInt(String str) throws NumberFormatException {
        if (str == null) {
            throw new NumberFormatException("null string");
        }
        return Integer.parseInt(str);
    }

    /**
     * Auxiliary static function to parse a long from a string and handles null strings
     * @param str the string with the number
     * @return the long value
     * @throws NumberFormatException if string is not valid
     */
    public static long parseLong(String str) throws NumberFormatException {
        if (str == null) {
            throw new NumberFormatException("null string");
        }
        return Long.parseLong(str);
    }
    
    /**
     * Compares the priority with another event, for scheduling purposes
     * @param ev the other event
     * @return true if the event has more priority, false otherwise;
     */
    public boolean has_higher_priority(Event ev) {
        return (kind > ev.kind());
    }
    
    /**
     * Constructor of Event object
     * @param log object that implements the Log interface
     * @param type Event kind
     * @param time simulation time
     */
    public Event(Log log, int type, long time) {
        this.log = log;
        this.kind = type;
        this.time = time;
        this.frame= null;
    }
    
    /**
     * Get the kind of the event object
     * @return the kind of the event
     */
    public int kind() {
        return kind;
    }
    
    /**
     * Sets the kind of the event object
     * @param _kind the kind of the event object
     */
    public void set_kind(int _kind) {
        this.kind= _kind;
    }
    
    /**
     * Get the current simulation time
     * @return the simulation time
     */
    public long time() {
        return time;
    }
    
    /**
     * Set the simulation time
     * @param time the simulaton time
     */
    public void set_time(long time) {
        this.time= time;    
    }
    
    /**
     * Used with FRAME_EVENT to get the frame contents
     * @return the frame contents
     */
    public Frame frame() {
        return frame;
    }
    
    /**
     * Used with FRAME_EVENT to set the frame contents
     * @param frame the frame contents
     */
    public void set_frame(Frame frame) {
        this.frame= frame;
    }

    /**
     * Used with TIMER_EVENT or STAT_EVENT to get the key parameter
     * @return the key value
     */
    public int key() {
        return key;
    }

    /**
     * Used with TIMER_EVENT or STAT_EVENT to set the key parameter
     * @param key the key value
     */
    public void set_key(int key) {
        this.key= key;
    }

    /**
     * Used with TIMER_EVENT to get the interval duration
     * @return the interval duration
     */
    public long interval() {
        return interval;
    }
    
    /**
     * Used with CONFIGURATION events to set the configuration parameters
     * @param protocol protocol number
     * @param max_seq maximum sequence number
     * @param swnd sending window size
     * @param rwnd receiving window size
     * @param timeout data timeout value
     * @param packets number of packets transmitted
     */
    public void set_configuration(int protocol, int max_seq, int swnd, int rwnd, 
            long timeout, int packets) {
        this.protocol= protocol;
        this.max_seq= max_seq;
        this.swnd= swnd;
        this.rwnd= rwnd;
        this.timeout= timeout;
        this.packets= packets;
    }
    
    /**
     * Used with CONFIGURATION events to get the protocol number
     * @return the protocol number
     */
    public int protocol() {
        return protocol;
    }
    
    /**
     * Used with CONFIGURATION events to get the maximum sequence number
     * @return the maximum sequence number
     */
    public int max_seq() {
        return max_seq;
    }
    
    /**
     * Used with CONFIGURATION events to get the sending window value
     * @return the sending window value
     */
    public int swnd() {
        return swnd;
    }
    
    /**
     * Used with CONFIGURATION events to get the receiving window value
     * @return the receiving window value
     */
    public int rwnd() {
        return rwnd;
    }
    
    /**
     * Used with CONFIGURATION events to get the timeout value
     * @return the timeout value
     */
    public long timeout() {
        return timeout;
    }
    
    /**
     * Used with CONFIGURATION events to get the number of packets transmited
     * @return the number of packets transmited
     */
    public int packets() {
        return packets;
    }
    
    /**
     * Resets the event contents to UNDEFIED_EVENT
     */
    private void reset_event() {
        kind = UNDEFINED_EVENT;
        time = UNDEF_TIME;
        frame= null;
    }

    /**
     * Get a string with the kind of the event
     * @return string with the kind of event
     */
    public String kindString() {
        String str;
        switch (kind) {
            case TIME_EVENT:
                str = "TIME";
                break;
            case FRAME_EVENT:
                str = "FRAME";
                break;
            case CANCEL_FRAME_EVENT:
                str = "CANCEL_FRAME";
                break;
            case START_TIMER:
                str = "START_TIMER";
                break;
            case TIMER_EVENT:
                str = "TIMER_EVENT";
                break;
            case STOP_EVENT:
                str = "STOP_EVENT";
                break;
            case END_EVENT:
                str = "END_EVENT";
                break;
            case DATA_END:
                str = "DATA_END";
                break;
            case STAT_EVENT:
                str = "STAT_EVENT";
                break;
            case REQ_CONFIG:
                str = "REQ_CONFIG";
                break;
            case CONFIGURATION:
                str = "CONFIGURATION";
                break;
            case UNDEFINED_EVENT:
                str = "UNDEFINED";
                break;
            default:
                return null;
        }
        return str;
    }

    /**
     * Returns a string with the event object contents
     * @return string with the event object contents
     */
    @Override
    public String toString() {
        String str = kindString();
        str += ((time == UNDEF_TIME) ? "(undef)" : ("(" + time + ")"));
        switch (kind) {
            case FRAME_EVENT:
                str += " " + ((frame != null) ? frame.toString() : " null frame");
                break;
            case CANCEL_FRAME_EVENT:
                str += " cancel " + ((frame != null) ? frame.toString() : " null frame");
                break;
            case START_TIMER:
                str += " " + interval + " " + key;
                break;
            case TIMER_EVENT:
                str += " " + key;
                break;
            case DATA_END:
                str += " " + key;
                break;
            case STAT_EVENT:
                switch (key) {
                    case Event.STAT_PAYLOADS_RX:
                        str += " Payloads_RX";
                        break;
                    case Event.STAT_PAYLOADS_TX:
                        str += " Payloads_TX";
                        break;
                    case Event.STAT_RETRANSMITED:
                        str += " Retransmissions";
                        break;
                    case Event.STAT_PAYLOADS_RX_BUFFERFULL:
                        str += " BufferFull";
                        break;
                }   break;
            case CONFIGURATION:
                str += " proto "+protocol+" max_seq "+max_seq+" SWND "+swnd+
                        " RWND "+rwnd+" Timeout "+timeout+" Packets "+packets;
                break;
            default:
                break;
        }
        return str;
    }

    /**
     * Prepares a string with the event contents, serializing the object
     * @return the string, or null if error
     */
    public String event_to_str() {
        String line = time + " " + kindString();
        if ((line == null) || (kind == UNDEFINED_EVENT)) {
            return null;
        }
        try {
            switch (kind) {
                case FRAME_EVENT:
                    line += " " + frame.frame_to_str();
                    break;
                case CANCEL_FRAME_EVENT:
                    line += " " + frame.frame_to_str();
                    break;
                case START_TIMER:
                    line += " " + interval + " " + key;
                    break;
                case TIMER_EVENT:
                case STAT_EVENT:
                case DATA_END:
                    line += " " + key;
                    break;
                case CONFIGURATION:
                    line += " "+protocol+" "+max_seq+" "+swnd+" "+rwnd+" "+
                            timeout+" "+packets;
                    break;
            }
            return line;
        } catch (Exception e) {
            log.Log("Error in event_to_str: " + e + "\n");
            return null;
        }
    }
    
    

    /**
     * Decodes the contents of a string to the event, desirealizing it
     * @param line - string with the event's contents
     * @param log - Log object
     * @return true if decoded successfully, false otherwise
     */
    public boolean str_to_event(String line, Log log) {
        if (line == null) {
            return false;
        }
        StringTokenizer st = new StringTokenizer(line);
        String cmd = null;
        
        try {
            if (st.countTokens() < 2) {
                log.Log("Event with invalid number of tokens\n");
                return false;
            }
            
            // parse time and event name
            String stime= st.nextToken();      // Time
            cmd= st.nextToken();        // Event name
            time= parseLong(stime);
            
            switch (cmd) {
                case "TIME":
                    if (st.countTokens() != 0) {
                        log.Log("TIME event with parameters\n");
                        reset_event();
                        return false;
                    }   kind= TIME_EVENT;
                    break;
                case "FRAME":
                    if (st.countTokens() == 0) {
                        log.Log("FRAME event without parameters\n");
                        reset_event();
                        return false;
                    }   // Get the remaining line
                    String param=
                            line.substring(line.indexOf(cmd)+cmd.length()).trim();
                    frame= new Frame();
                    frame.str_to_frame(param, log);
                    if (frame.is_undef()) {
                        log.Log("Invalid Frame contents\n");
                        reset_event();
                        return false;
                    }   kind= FRAME_EVENT;
                    break;
                case "CANCEL_FRAME":
                    if (st.countTokens() == 0) {
                        log.Log("CANCEL_FRAME event without parameters\n");
                        reset_event();
                        return false;
                    }   // Get the remaining line
                    param= line.substring(line.indexOf(cmd)+cmd.length()).trim();
                    frame= new Frame();
                    frame.str_to_frame(param, log);
                    if (frame.is_undef()) {
                        log.Log("Invalid Frame contents\n");
                        reset_event();
                        return false;
                    }   kind= CANCEL_FRAME_EVENT;
                    break;
                case "START_TIMER":
                    if (st.countTokens() != 2) {
                        log.Log("SET_TIME event without interval or key\n");
                        reset_event();
                        return false;
                    }   interval= parseLong(st.nextToken());
                    key= parseInt(st.nextToken());
                    kind= START_TIMER;
                    break;
                case "TIMER_EVENT":
                    if (st.countTokens() != 1) {
                        log.Log("TIME event without key\n");
                        reset_event();
                        return false;
                    }   key= parseInt(st.nextToken());
                    kind= TIMER_EVENT;
                    break;
                case "STOP_EVENT":
                    if (st.countTokens() != 0) {
                        log.Log("STOP event with parameters\n");
                        reset_event();
                        return false;
                    }   kind= STOP_EVENT;
                    break;
                case "END_EVENT":
                    if (st.countTokens() != 0) {
                        log.Log("END event with parameters\n");
                        reset_event();
                        return false;
                    }   kind= END_EVENT;
                    break;
                case "DATA_END":
                    if (st.countTokens() != 1) {
                        log.Log("DATA_END event without key\n");
                        reset_event();
                        return false;
                    }   key= parseInt(st.nextToken());
                    kind= DATA_END;
                    break;
                case "STAT_EVENT":
                    if (st.countTokens() != 1) {
                        log.Log("STAT_TIME event without key\n");
                        reset_event();
                        return false;
                    }   key= parseInt(st.nextToken());
                    kind= STAT_EVENT;
                    break;
                case "REQ_CONFIG":
                    if (st.countTokens() != 0) {
                        log.Log("REQ_CONFIG event with parameters\n");
                        reset_event();
                        return false;
                    }   kind= REQ_CONFIG;
                    break;
                case "CONFIGURATION":
                    if (st.countTokens() != 6) {
                        log.Log("CONFIGURATION event with invalid number of parameters\n");
                        reset_event();
                        return false;
                    }   protocol= parseInt(st.nextToken());
                    max_seq= parseInt(st.nextToken());
                    swnd= parseInt(st.nextToken());
                    rwnd= parseInt(st.nextToken());
                    timeout= parseLong(st.nextToken());
                    packets= parseInt(st.nextToken());
                    kind= CONFIGURATION;
                    break;
                default:
                    log.Log("Invalid event type\n");
                    reset_event();
                    return false;
            }
            return true;
        } catch (NumberFormatException ne) {
            log.Log("Invalid number in " + (cmd == null ? "" : cmd) + " element\n");
            reset_event();
            return false;
        } catch (Exception e) {
            log.Log("Exception in " + (cmd == null ? "" : cmd) + " element: " + e + "\n");
            reset_event();
            return false;
        }
    }    
    
    
    
    /**
     * Auxiliary variable to log messages
     */
    private final Log log;
    /**
     * Event kind
     */
    private int kind;
    /**
     * Current Time
     */
    private long time;
    /**
     * Frame contents; defined when kind is FRAME_EVENT
     */
    private Frame frame;
    /**
     * Timer key; used if kind is TIMER_EVENT or SET_TIMER
     */
    private int key;
    /**
     * Timer interval; used when kind is SET_TIMER; -1 cancels timer
     */
    private long interval;
    /**
     * Configuration data; used in CONFIGURATION events
     */
    private int protocol; // Protocol number
    private int max_seq;  // Maximum seqence number
    private int swnd;     // Sending window
    private int rwnd;     // Receiving window
    private long timeout; // delay
    private int packets;  // number of packets
}
