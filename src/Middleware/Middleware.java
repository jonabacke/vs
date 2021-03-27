package Middleware;

import Config.ConfigFile;
import Config.NetworkTuple;
import FindPartner.FindPartner;
import Lamport.LamportMutex;
import Lamport.Request;
import Lamport.TCPClient;
import Lamport.TCPServer;
import RobotApplication.Partner;
import RobotApplication.Robot;
import FindPartner.MulticastClient;

import java.io.BufferedReader;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class Middleware implements IMiddlewareInvoke, IMiddlewareRegisterService{
    private static final Logger logger = Logger.getGlobal();

    private final TCPServer tcpServer;
    private final CallHandler callHandler;
    private LamportMutex lamportMutex;
    private FindPartner findPartner;
    private Map<UUID, NetworkTuple> partner;
    private PriorityQueue<Partner> partnerRobotsQueue;
    private UUID uuid;
    private boolean isReliable;
    private final Map<UUID, TCPClient> tcpClients;
    private MulticastClient client;


    public Middleware() {
        this.tcpServer = new TCPServer();
        this.client = new MulticastClient();
        this.partnerRobotsQueue = new PriorityQueue<>();
        this.tcpClients = new HashMap<>();
        this.callHandler = new CallHandler();
    }

    public void setPartner(Map<UUID, NetworkTuple> partner) {
        this.partner = partner;
        for (Map.Entry<UUID, NetworkTuple> tuple : partner.entrySet()) {
            TCPClient tcpClient = new TCPClient(tuple.getValue().getIp(), tuple.getValue().getPort());
            tcpClients.put(tuple.getKey(), tcpClient);
        }

    }

    public TCPServer getTcpServer() {
        return tcpServer;
    }

    @Override
    public void register(String serviceName, IMiddlewareCallableStub stub, boolean isReliable) {
        this.callHandler.register(new Service(serviceName, isReliable, this.tcpServer), stub);
    }

    @Override
    public void invoke(UUID partnerUUID, Class<?> className, String function, Object [] values, boolean isReliable) {
        String msg =  Marshaller.pack(className, function, values);
        Logger.getGlobal().info(msg);
        if (isReliable) {
            this.tcpClients.get(partnerUUID).sendMessage(msg);
        } else {
            this.client.publishMsg(msg);
        }




        /**
         *
        switch (serviceName){
            case ConfigFile.REGISTER -> {
                this.uuid = UUID.fromString(value);
                this.findPartner = new FindPartner(this.tcpServer.getIp(), this.tcpServer.getPort(), uuid);
                this.partner = this.findPartner.getPartner();
                this.setUpPriorityQueue();
                this.lamportMutex = new LamportMutex(this.tcpServer, this.partner, uuid);
            }
            case ConfigFile.WELDING -> {
                this.lamportMutex.startCircle();
                this.lamportMutex.requestToEnter();
                while (!lamportMutex.allowedToEnter()) {
                    this.sleep();
                }
                for (int i = 0; i < Robot.class.getDeclaredMethods().length; i ++) {
                    if (Robot.class.getDeclaredMethods()[i].getName().equals(ConfigFile.START_WORKING)){
                        this.callBack(Robot.class.getName(), ConfigFile.START_WORKING, new Object[0], Robot.class.getDeclaredMethods()[i].getParameterTypes());
                        break;
                    }
                }
                this.lamportMutex.release();
                while (this.lamportMutex.isDashed()) {
                    this.sleep();
                }
            }
            case ConfigFile.NOT_WELDING -> {
                this.lamportMutex.startCircle();
                while (this.lamportMutex.isDashed()) {
                    // TODO ask if someone has error
                    this.sleep();
                }
            }
        }
        this.sleep();
         */
    }


}

