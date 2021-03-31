package RobotApplication;

import Config.ConfigFile;
import Config.NetworkTuple;
import FindPartner.IFindPartner;
import Lamport.ILamportMutex;
import Lamport.Request;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import static java.lang.System.currentTimeMillis;

public class Controller implements IWeldingRobotCall, IControllerCall {
    private static final Logger logger = Logger.getGlobal();
    private final ReentrantLock mutex = new ReentrantLock();
    private final IControllerInvoke controllerInvoke;
    private final ILamportMutex lamportMutex;
    private final IFindPartner findPartner;
    private final Queue<Partner> partnerRobotsQueue;
    private final AtomicBoolean emergencyCall;
    private boolean running;
    private IWedingRobotInvoke weldingRobot;
    private Map<UUID, NetworkTuple> partner;
    private UUID uuid;

    public Controller(UUID uuid, IControllerInvoke controllerInvoke, ILamportMutex lamportMutex, IFindPartner findPartner) {
        this.uuid = uuid;
        this.controllerInvoke = controllerInvoke;
        this.lamportMutex = lamportMutex;
        this.findPartner = findPartner;
        this.partnerRobotsQueue = new PriorityQueue<>();
        this.running = true;
        this.emergencyCall = new AtomicBoolean(false);
    }

    public void init(IWedingRobotInvoke weldingRobot) {
        this.weldingRobot = weldingRobot;
    }

    @Override
    public void register (int id) {
        if (id != 0) {
            this.uuid = UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).array());
        }
        this.lamportMutex.setProcID(this.uuid);
        this.partner = this.findPartner.getPartner(this.uuid);
        if (partner.size() < 3) {
            throw new IllegalArgumentException("zu wenig Robos" + partner.size());
        }
        this.lamportMutex.setPartner(this.partner);
        this.controllerInvoke.setPartner(this.partner);
        for (Map.Entry<UUID, NetworkTuple> tuple : this.partner.entrySet()) {
            logger.warning("Hallo " + tuple.getKey() + ", I am " + this.uuid.toString());
            this.partnerRobotsQueue.add(new Partner(tuple.getKey(), tuple.getValue()));
        }
        sleep(ConfigFile.WAITING_TIME);
        this.startCircles(ConfigFile.CIRCLE_AMOUNT);
    }

    private void startCircles(int amount) {
        long old = currentTimeMillis();
        while (amount > 0 && this.running) {
        logger.warning("Start Circle");
            if (this.checkFirstThreeElements(ConfigFile.AMOUNT_WORKER)) {
                this.work();
            } else {
                while (this.lamportMutex.isRunning() && this.running && !this.emergencyCall.get()) {
                    sleep(0);
                }
            }
            if (this.emergencyCall.get()) {
                logger.severe("Start emergency work");
                this.emergencyCall.set(false);
                this.work();
                logger.severe("End emergency work");
            }
            amount --;
            logger.warning("End Circle: " + amount);
        }
        logger.severe("Finish in " + (currentTimeMillis() - old) + " ms");
        List<Partner> temp = new ArrayList<>(this.partnerRobotsQueue);
        Collections.sort(temp);
        for (Partner p: temp) {
            logger.severe("Robot: " + p.toString());
        }
        if (ConfigFile.LOG){
            this.writeOut();
        }
    }

    private void work() {
        this.lamportMutex.requestToEnter();
        while (this.lamportMutex.allowedToEnter()) {
            this.sleep(0);
        }
        if ((int) (Math.random() * 100) < 1) {
            logger.severe("Got Error " + this.uuid);
            this.weldingRobot.setStatus(-1);
            this.lamportMutex.gotError();
            for (UUID uuid : this.partner.keySet()) {
                controllerInvoke.gotError(uuid, this.uuid);
            }
        } else {
            this.startWorking();
            this.lamportMutex.release();
        }
        while (this.lamportMutex.isRunning() && this.running) {
            sleep(0);
        }
    }

    public void writeOut() {
        Queue<Request> queue = this.lamportMutex.getLoggingQueue();
        try {
            FileWriter fileWriter = new FileWriter("log/" + this.uuid.toString());
            for (Request r : queue) {
                fileWriter.write(r.toString() + "\r");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void startWorking() {
        this.weldingRobot.setStatus(1);
        this.weldingRobot.welding();
        this.weldingRobot.setStatus(0);
    }

    private boolean checkFirstThreeElements(int amountWorker) {
        this.mutex.lock();
        if (this.partnerRobotsQueue.size() < 3) throw new IllegalArgumentException("" + this.partnerRobotsQueue.size());
        boolean result = false;
        int counter = 0;
        while (counter < amountWorker) {
            Partner temp = this.partnerRobotsQueue.poll();
            assert temp != null;
            temp.setWorkedCounter(temp.getWorkedCounter() + 1);
            if (temp.getUuid().equals(this.uuid)) {
                result = true;
            }
            this.partnerRobotsQueue.add(temp);
            counter ++;
        }
        this.mutex.unlock();
        return result;
    }

    private void sleep(int offset) {
        try {
            Thread.sleep(10 + offset);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void errorReceive(String uuidString) {
        logger.severe("Error received from " + uuidString);
        this.mutex.lock();
        if (this.partnerRobotsQueue.size() <= ConfigFile.AMOUNT_WORKER) {
            this.running = false;
            logger.severe("Nicht genung Roboter zum schweißen verfügbar");
        }
        this.partnerRobotsQueue.removeIf(x -> x.getUuid().toString().equals(uuidString));
        this.mutex.unlock();
        if (this.checkFirstThreeElements(1)) {
            this.emergencyCall.set(true);
        }
    }
}
