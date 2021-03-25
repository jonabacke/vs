package Lamport;

import Config.NetworkTuple;

import java.io.BufferedReader;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class LamportMutex implements ILamportMutex {
    private static final Logger logger = Logger.getGlobal();

    private final Map<UUID, NetworkTuple> partner;
    private final Map<UUID, TCPClient> tcpClients = new HashMap<>();
    private final TCPServer tcpServer;
    private final UUID procID;
    private final ReentrantLock mutex = new ReentrantLock();
    private final boolean [] dash = {false, false, false};
    private AtomicInteger clock;
    private Queue<Request> queue;
    private boolean running;

    public LamportMutex(TCPServer tcpServer, Map<UUID, NetworkTuple> partner, UUID uuid) {
        if (tcpServer == null || partner == null) throw new IllegalArgumentException();
        this.tcpServer = tcpServer;
        this.partner = partner;
        this.procID = uuid;
        for (Map.Entry<UUID, NetworkTuple> tuple : partner.entrySet()) {
            TCPClient tcpClient = new TCPClient(tuple.getValue().getIp(), tuple.getValue().getPort());
            tcpClients.put(tuple.getKey(), tcpClient);
        }
        this.init();
    }

    @Override
    public void init() {
        this.clock = new AtomicInteger(0);
        this.queue = new PriorityQueue<>();
        this.running = false;
        this.receive();
    }

    @Override
    public void requestToEnter() {
        this.mutex.lock();
        this.queue.add(new Request(this.clock.incrementAndGet(), this.procID, MsgEnum.ENTER));
        this.cleanupQ();
        for (TCPClient tcpClient : tcpClients.values()) {
            tcpClient.sendMessage(new Request(this.clock.get(), this.procID, MsgEnum.ENTER).getNetworkString());
        }
        this.mutex.unlock();
    }

    @Override
    public void allowToEnter(UUID requester) {
        try {
            Request r = new Request(this.clock.incrementAndGet(), this.procID, MsgEnum.ALLOW);
            tcpClients.get(requester).sendMessage(r.getNetworkString());
        } catch (NullPointerException exception) {
            logger.warning("Requester: " + requester.toString());
        }
    }

    @Override
    public void release() {
        this.mutex.lock();
        this.queue.poll();
        this.cleanupQ();
        this.clock.incrementAndGet();
        for (TCPClient tcpClient : tcpClients.values()) {
            tcpClient.sendMessage(new Request(this.clock.get(), this.procID, MsgEnum.RELEASE).getNetworkString());
        }
        this.mutex.unlock();
        for (boolean d: this.dash) {
            if (d) {
                d = false;
                break;
            }
        }
    }

    @Override
    public boolean allowedToEnter() {
        try {
            this.mutex.lock();
            long commProcess = this.queue.stream().filter(x -> x.getMsgType().equals(MsgEnum.ALLOW)).count();
            if (this.queue.peek() == null) {
                return false;
            } else {
                return this.queue.peek().getProcID().equals(this.procID) && this.partner.size() == commProcess;
            }
        } finally {
            this.mutex.unlock();
        }
    }

    @Override
    public void receive() {
        this.running = true;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.running = false));

        new Thread(() -> {
            ExecutorService executorService = Executors.newFixedThreadPool(20);
            while (this.running) {
                Socket socket = this.tcpServer.initServer();
                BufferedReader bufferedReader = tcpServer.receiveMessages(socket);
                executorService.execute(() -> {
                    Request msg;
                    while (this.running) {
                        String received = tcpServer.readLine(bufferedReader);
                        msg = new Request(received);
                        logger.info("Message: " + msg.toString() + " : " + Thread.currentThread().getName());
                        this.mutex.lock();
                        this.clock.set(Math.max(this.clock.get(), msg.getClock()));
                        this.clock.incrementAndGet();
                        switch (msg.getMsgType()) {
                            case ENTER:
                                if (!msg.getProcID().toString().equals(this.procID.toString())) {
                                    this.queue.add(msg);
                                }
                                this.allowToEnter(msg.getProcID());
                                break;
                            case ALLOW:
                                this.queue.add(msg);
                                break;
                            case RELEASE:
                                if (this.queue.size() > 0 && !this.queue.peek().getProcID().toString().equals(this.procID.toString())) {
                                    Request finalMsg = msg;
                                    this.queue.removeIf(x -> x.getMsgType().equals(MsgEnum.ENTER) && finalMsg.getProcID().toString().equals(x.getProcID().toString()));
                                }
                                break;
                            default:
                                break;
                        }
                        logger.info("Queue:  " + this.queue.toString());
                        this.mutex.unlock();
                    }
                });
            }
            logger.warning("shutdown");
        }).start();
    }

    private void cleanupQ() {
        this.queue.removeIf(x -> x.getMsgType().equals(MsgEnum.ALLOW) && x.getClock() <= this.clock.get());
    }

    @Override
    public void startCircle() {
        for (boolean d: this.dash) {
            d = true;
        }
    }

    @Override
    public boolean isDashed() {
        boolean result = false;
        for (boolean d : this.dash) {
            result = result || d;
        }
        return result;
    }
}
