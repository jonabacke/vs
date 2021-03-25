package Middleware;

import Config.ConfigFile;
import Config.NetworkTuple;
import FindPartner.FindPartner;
import Lamport.LamportMutex;
import Lamport.TCPServer;
import RobotApplication.Partner;
import RobotApplication.Robot;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.logging.Logger;

public class Middleware implements IMiddlewareInvoke, IMiddlewareRegisterService{
    private static final Logger logger = Logger.getGlobal();

    private final TCPServer tcpServer;
    private LamportMutex lamportMutex;
    private FindPartner findPartner;
    private Map<UUID, NetworkTuple> partner;
    private Map<String, IMiddlewareCallableStub> services;
    private PriorityQueue<Partner> partnerRobotsQueue;
    private UUID uuid;

    public Middleware() {
        this.tcpServer = new TCPServer();
        this.services = new HashMap<>();
        this.partnerRobotsQueue = new PriorityQueue<>();
    }


    @Override
    public void invoke(String serviceName, String value) {
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
                    this.sleep();
                }
            }
        }
        this.sleep();
    }

    @Override
    public void register(String serviceName, IMiddlewareCallableStub stub) {
        this.services.put(serviceName, stub);
    }

    public void callBack(String serviceName, String methodName, Object [] objectParams, Class<?> [] classParams) {
        this.services.get(serviceName).call(methodName, objectParams, classParams);
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setUpPriorityQueue() {
        for (Map.Entry<UUID, NetworkTuple> tuple : this.partner.entrySet()) {
            logger.info("Hallo " + tuple.getKey() + ", I am " + this.uuid.toString());
            this.partnerRobotsQueue.add(new Partner(tuple.getKey(), tuple.getValue()));
        }
        for (int i = 0; i < Robot.class.getDeclaredMethods().length; i ++) {
            if (Robot.class.getDeclaredMethods()[i].getName().equals(ConfigFile.SET_PARTNER_ROBOTS_QUEUE)) {
                Object [] params = new Object[1];
                Partner [] partners = this.partnerRobotsQueue.toArray(new Partner[0]);
                params[0] = partners;
                this.callBack(Robot.class.getName(), ConfigFile.SET_PARTNER_ROBOTS_QUEUE, params, Robot.class.getDeclaredMethods()[i].getParameterTypes());
                break;
            }
        }
    }
}

