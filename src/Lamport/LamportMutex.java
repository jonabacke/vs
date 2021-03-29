package Lamport;

import Config.ConfigFile;
import Config.NetworkTuple;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class LamportMutex implements ILamportMutex {
    private static final Logger logger = Logger.getGlobal();

    private Map<UUID, NetworkTuple> partner;
    private UUID procID;
    private final ReentrantLock mutex = new ReentrantLock();
    private AtomicInteger clock;
    private Queue<Request> queue;
    private Queue<Request> loggingQueue;
    private final LamportInvoke lamportInvoke;
    private int semaphore;

    public LamportMutex(LamportInvoke lamportInvoke) {
        this.semaphore = 0;
        this.lamportInvoke = lamportInvoke;
        this.partner = new HashMap<>();
        this.init();
    }

    public Queue<Request> getLoggingQueue() {
        try {
            this.mutex.lock();
            return loggingQueue;
        }  finally {
            this.mutex.unlock();
        }
    }

    public void setProcID(UUID procID) {
        this.procID = procID;
    }

    public void setPartner(Map<UUID, NetworkTuple> partner) {
        this.partner = partner;
    }

    public void gotError() {
        this.mutex.lock();
        for (UUID uuid : partner.keySet()) {
            this.lamportInvoke.send(uuid, new Request(this.clock.get(), this.procID, MsgEnum.ERROR).getNetworkString());
        }
        this.mutex.unlock();
    }

    @Override
    public void init() {
        this.clock = new AtomicInteger(0);
        this.queue = new PriorityQueue<>();
        this.loggingQueue = new PriorityQueue<>();
    }

    @Override
    public void requestToEnter() {
        this.mutex.lock();
        Request r = new Request(this.clock.incrementAndGet(), this.procID, MsgEnum.ENTER);
        this.queue.add(r);
        this.cleanupQ();
        for (UUID uuid : partner.keySet()) {
            this.lamportInvoke.send(uuid, r.getNetworkString());
        }
        logger.info("Queue:  " + this.queue.toString());
        this.mutex.unlock();
    }

    @Override
    public void allowToEnter(UUID requester) {
        Request r = new Request(this.clock.incrementAndGet(), this.procID, MsgEnum.ALLOW);
        this.lamportInvoke.send(requester, r.getNetworkString());
    }

    @Override
    public void release() {
        this.mutex.lock();
        this.cleanupQ();
        this.clock.incrementAndGet();
        for (UUID uuid : partner.keySet()) {
            this.lamportInvoke.send(uuid, new Request(this.clock.get(), this.procID, MsgEnum.RELEASE).getNetworkString());
        }
        this.mutex.unlock();
    }

    @Override
    public boolean allowedToEnter() {
        try {
            this.mutex.lock();
            long commProcess = this.queue.stream().filter(x -> x.getMsgType().equals(MsgEnum.ALLOW)).count();
            if (this.queue.peek() == null) {
                return true;
            } else {
                return !this.queue.peek().getProcID().equals(this.procID) || this.partner.size() != commProcess;
            }
        } finally {
            this.mutex.unlock();
        }
    }

    @Override
    public void receive(String msg) {
        Request request = new Request(msg);
        if (ConfigFile.LOG) {
            this.loggingQueue.add(request);
        }
        this.mutex.lock();
        logger.info("Message: " + request.toString() + " : " + Thread.currentThread().getName());
        this.clock.set(Math.max(this.clock.get(), request.getClock()));
        this.clock.incrementAndGet();
        switch (request.getMsgType()) {
            case ENTER:
                if (!request.getProcID().toString().equals(this.procID.toString())) {
                    this.queue.add(request);
                }
                this.allowToEnter(request.getProcID());
                break;
            case ALLOW:
                this.queue.add(request);
                break;
            case RELEASE:
                if (this.queue.size() > 0) {
                    boolean worked = this.queue.removeIf(x -> x.getMsgType().equals(MsgEnum.ENTER) && request.getProcID().toString().equals(x.getProcID().toString()));
                    if (worked) {
                        this.semaphore ++;
                    } else {
                        logger.severe("Failed to release" + this.queue.toString());
                    }
                }
                break;
            case ERROR:
                if (this.queue.size() > 0) {
                    this.queue.removeIf(x -> x.getMsgType().equals(MsgEnum.ENTER) && request.getProcID().toString().equals(x.getProcID().toString()));
                }
            default:
                break;
        }
        logger.info("Queue:  " + this.queue.toString());
        this.mutex.unlock();
    }

    private void cleanupQ() {
        this.queue.removeIf(x -> x.getMsgType().equals(MsgEnum.ALLOW) && x.getClock() <= this.clock.get());
    }

    @Override
    public void resetCircle() {
        this.semaphore -= ConfigFile.AMOUNT_WORKER;
    }

    public boolean isRunning() {
        mutex.lock();
        boolean result = this.semaphore < ConfigFile.AMOUNT_WORKER;
        logger.info("Semaphore:  " + this.semaphore);
        if (!result) {
            this.resetCircle();
        }
        mutex.unlock();
        return result;
    }
}
