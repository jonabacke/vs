package RobotApplication;

import Config.ConfigFile;
import Config.NetworkTuple;
import FindPartner.FindPartner;
import Lamport.LamportMutex;
import Lamport.TCPServer;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;

public class Robot implements IWeldingRobot, IRobotCall {
    private static final Logger logger = Logger.getGlobal();
    private final RobotInvoke robotInvoke;
    private UUID uuid;
    private Queue<Partner> partnerRobotsQueue;
    private IWeldingRobot weldingRobot;

    public Robot(UUID uuid, RobotInvoke robotInvoke) {
        this.uuid = uuid;
        this.robotInvoke = robotInvoke;
    }

    public void init(IWeldingRobot weldingRobot) {
        this.weldingRobot = weldingRobot;
    }


    private void startCircles(int amount) {
        long old = currentTimeMillis();
        while (amount > 0) {
        logger.info("Start Circle");
            if (this.checkFirstThreeElements()) {
                this.robotInvoke.welding();
            } else {
                this.robotInvoke.notWelding();
            }
            amount --;
        logger.info("End Circle");
        }

        logger.info("Finish in " + (currentTimeMillis() - old) + " ms");

    }
    @Override
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

    @Override
    public void register (int id) {
        if (id != 0) {
            this.uuid = UUID.nameUUIDFromBytes(ByteBuffer.allocate(4).array());
        }
        this.partnerRobotsQueue = new PriorityQueue<>();
        this.robotInvoke.register(this.uuid.toString());
        this.startCircles(ConfigFile.CIRCLE_AMOUNT);
    }
    @Override
    public void setPartnerRobotsQueue(Partner[] partnerRobotsQueue) {
        this.partnerRobotsQueue.addAll(Arrays.asList(partnerRobotsQueue));
    }

    private boolean checkFirstThreeElements() {
        if (this.partnerRobotsQueue == null) throw new IllegalArgumentException();
        boolean result = false;
        int counter = 0;
        while (counter < 3) {
            Partner temp = this.partnerRobotsQueue.poll();
            assert temp != null;
            temp.setWorkedCounter(temp.getWorkedCounter() + 1);
            if (temp.getUuid().equals(this.uuid)) {
                result = true;
            }
            this.partnerRobotsQueue.add(temp);
            counter ++;
        }
        return result;
    }
}
