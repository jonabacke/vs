package Lamport;

import Config.NetworkTuple;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public interface ILamportMutex {

    /**
     * send enter request to others
     */
    void requestToEnter();


    /**
     * release all allows of other processes and release mutex
     */
    void release();

    /**
     * allow other process to enter
     *
     * @return true if enter is allowed
     */
    boolean allowedToEnter();

    void gotError();

    void setProcID(UUID procID);

    void setPartner(Map<UUID, NetworkTuple> partner);

    boolean isRunning();

    Queue<Request> getLoggingQueue();
}
