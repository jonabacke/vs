package RobotApplication;

import Config.ConfigFile;
import Config.NetworkTuple;
import FindPartner.FindPartner;
import Lamport.LamportMutex;
import Lamport.Request;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import static java.lang.System.currentTimeMillis;

public class Controller implements IWeldingRobot, IRobotCall {
    private static final Logger logger = Logger.getGlobal();
    private final RobotInvoke robotInvoke;
    private UUID uuid;
    private final Queue<Partner> partnerRobotsQueue;
    private IWeldingRobot weldingRobot;
    private final LamportMutex lamportMutex;
    private Map<UUID, NetworkTuple> partner;
    private final FindPartner findPartner;
    private boolean running;
    private final ReentrantLock mutex = new ReentrantLock();
    private final AtomicBoolean emergencyCall;

    public Controller(UUID uuid, RobotInvoke robotInvoke, LamportMutex lamportMutex, FindPartner findPartner) {
        this.uuid = uuid;
        this.robotInvoke = robotInvoke;
        this.lamportMutex = lamportMutex;
        this.findPartner = findPartner;
        this.partnerRobotsQueue = new PriorityQueue<>();
        this.running = true;
        this.emergencyCall = new AtomicBoolean(false);
    }

    public void init(IWeldingRobot weldingRobot) {
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
        this.robotInvoke.setPartner(this.partner);
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
                this.lamportMutex.requestToEnter();
                while (this.lamportMutex.allowedToEnter()) {
                    this.sleep(0);
                }
                if ((int) (Math.random() * 100) < 1) {
                    logger.severe("Got Error " + this.uuid);
                    this.setStatus(-1);
                    this.lamportMutex.gotError();
                    for (UUID uuid : this.partner.keySet()) {
                        robotInvoke.gotError(uuid, this.uuid);
                    }
                } else {
                    this.startWorking();
                    this.lamportMutex.release();
                }
            }
            while (this.lamportMutex.isRunning() && this.running && !this.emergencyCall.get()) {
                sleep(0);
            }
            if (this.emergencyCall.get()) {
                logger.severe("Start emergency work");
                this.emergencyCall.set(false);
                this.lamportMutex.requestToEnter();
                while (this.lamportMutex.allowedToEnter()) {
                    this.sleep(0);
                }
                if ((int) (Math.random() * 100) <= 1) {
                    this.lamportMutex.gotError();
                    logger.severe("Got Error " + this.uuid);
                    for (UUID uuid : this.partner.keySet()) {
                         robotInvoke.gotError(uuid, this.uuid);
                    }
                } else {
                    this.startWorking();
                    this.lamportMutex.release();
                }
                logger.severe("End emergency work");
                while (this.lamportMutex.isRunning() && this.running && !this.emergencyCall.get()) {
                    sleep(0);
                }
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
        this.setStatus(1);
        this.welding();
        this.setStatus(0);
    }

    @Override
    public void setStatus(int status) {
        this.weldingRobot.setStatus(status);
    }

    @Override
    public void welding() {
        this.weldingRobot.welding();
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
    public void errorReceived(String uuidString) {
        logger.severe("Error received from " + uuidString);
        // TODO check if size < 3 -> End Program
        this.mutex.lock();
        if (this.partnerRobotsQueue.size() <= ConfigFile.AMOUNT_WORKER) {
            this.running = false;
            logger.severe("Nicht genung Roboter zum schweißen verfügbar");
        }
        // TODO delete at Queue
        this.partnerRobotsQueue.removeIf(x -> x.getUuid().toString().equals(uuidString));
        this.mutex.unlock();
        if (this.checkFirstThreeElements(1)) {
            this.emergencyCall.set(true);
        }
    }
}
