import java.util.UUID;

public interface ILamportMutex {
    /**
     * init mutex
     */
    public void init();
    
    /**
     * send enter request to outhers
     */
    public void requestToEnter();

     /**
      * allow to enter
      * @param requester who wants to enter
      */
    public void allowToEnter(UUID requester);

    /**
     * release all allows of outher processes and release mutex
     */
    public void release();

    /**
     * allow outher process to enter
     * @return
     */
    public boolean allowedToEnter();

    /**
     * receive msg
     */
    public void receive();
}
