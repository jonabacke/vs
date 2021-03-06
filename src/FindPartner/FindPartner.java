package FindPartner;

import Config.NetworkTuple;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static Config.ConfigFile.WAITING_TIME;

public class FindPartner implements IFindPartner {
    private static final Logger logger = Logger.getGlobal();
    private static final int COUNTER_MAX = 256;

    private final String tcpIP;
    private final int tcpPort;
    private final Map<UUID, NetworkTuple> partner;
    private final IFindPartnerInvoke findPartnerInvoke;
    private UUID uuid;
    private boolean receives = true;
    private int lessCounter = 0;

    public FindPartner(String tcpIP, int tcpPort, IFindPartnerInvoke findPartnerInvoke) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        }));
        this.partner = new HashMap<>();
        this.findPartnerInvoke = findPartnerInvoke;
        this.tcpIP = tcpIP;
        this.tcpPort = tcpPort;
    }

    private void publishStatus() {
        //TODO publish amount of known Robot.Partner
        String msg = new PartnerMessage(EPartnerMessage.STATUS, this.tcpIP, this.tcpPort, this.partner.size(), this.uuid).getNetworkString();
        this.findPartnerInvoke.publishMsg(msg);
    }

    private void requestSocketData() {
        // TODO publish connection request
        String msg = new PartnerMessage(EPartnerMessage.REQUEST, null, 0, 0, this.uuid).getNetworkString();
        this.findPartnerInvoke.publishMsg(msg);
    }

    private void reset() {
        String msg = new PartnerMessage(EPartnerMessage.RESET, null, 0, 0, this.uuid).getNetworkString();
        this.findPartnerInvoke.publishMsg(msg);
    }

    public void receivePublishedInformation(String receivedString) {
        this.receives = true;
        logger.finest(() -> "Received String: " + receivedString);
        PartnerMessage msg = new PartnerMessage(receivedString);
        if (msg.getMsgType().equals(EPartnerMessage.STATUS)) {
            this.addNewPartner(msg);
            this.checkAmount(msg);
        } else if (msg.getMsgType().equals(EPartnerMessage.REQUEST)) {
            this.publishStatus();
        } else if (msg.getMsgType().equals(EPartnerMessage.RESET)) {
            this.partner.clear();
            this.publishStatus();
        }
    }

    private void addNewPartner(PartnerMessage msg) {
        if (msg == null) throw new IllegalArgumentException();
        if (!this.partner.containsKey(msg.getUuid())) {
            this.partner.put(msg.getUuid(), new NetworkTuple(msg.getIp(), msg.getPort()));
        }
    }

    private void checkAmount(PartnerMessage msg) {
        if (msg == null) throw new IllegalArgumentException();
        if (msg.getAmount() < this.partner.size()) {
            // send I got more
            logger.finest(() -> "I got [" + this.partner.size() + "/" + msg.getAmount() + "] partner");
            this.publishStatus();
            this.lessCounter = 0;
        } else if (msg.getAmount() == this.partner.size()) {
            // send I got same
            logger.finest(() -> "I got [" + this.partner.size() + "/" + msg.getAmount() + "] partner");
            logger.finest(() -> "Robot.Partner: " + this.partner.keySet().toString());
        } else if (msg.getAmount() > this.partner.size()) {
            // send I got less -> need more
            logger.finest(() -> "I got [" + this.partner.size() + "/" + msg.getAmount() + "] partner");
            this.lessCounter++;
            if (lessCounter > COUNTER_MAX) {
                this.reset();
            } else {
                this.requestSocketData();
                this.publishStatus();
            }
        }
    }

    @Override
    public Map<UUID, NetworkTuple> getPartner(UUID uuid) {
        this.uuid = uuid;
        this.publishStatus();
        this.requestSocketData();
        this.waitForFinish();
        return partner;
    }

    private void waitForFinish() {
        while (this.receives) {
            this.receives = false;
            try {
                //noinspection BusyWait
                Thread.sleep(WAITING_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
