package Lamport;

import java.util.UUID;

public interface ILamportMutex {
    /**
     * init mutex
     */
    public void init();
    
    /**
     * send enter request to others
     */
    public void requestToEnter();

     /**
      * allow to enter
      * @param requester who wants to enter
      */
    public void allowToEnter(UUID requester);

    /**
     * release all allows of other processes and release mutex
     */
    public void release();

    /**
     * allow other process to enter
     * @return true if enter is allowed
     */
    public boolean allowedToEnter();

    /**
     * receive msg
     */
    public void receive(String msg);

    /**
     * start Cycle
     */
    public void resetCircle();

}
