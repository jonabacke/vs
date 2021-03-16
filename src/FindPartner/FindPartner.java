package FindPartner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import Config.NetworkTuple;

public class FindPartner {
    private static final Logger logger = Logger.getGlobal();

    private final String tcpIP;
    private final int tcpPort;
    private final UUID uuid;
    private final MulticastServer server;
    private final MulticastClient client;
    private boolean running = false;
    Map<UUID, NetworkTuple> partner;

    public FindPartner(String tcpIP, int tcpPort, UUID uuid) {
        Runtime.getRuntime().addShutdownHook(new Thread(()->this.running = false));
        this.partner = new HashMap<>();
        this.server = new MulticastServer();
        this.client = new MulticastClient();
        this.tcpIP = tcpIP;
        this.tcpPort = tcpPort;
        this.uuid = uuid;
        this.running = true;
        this.receivePublishedInformation();
        this.publishStatus();
        this.requestSocketData();
    }

    public void publishStatus() {
        //TODO publish amount of known Partner
        String msg = new PartnerMessage(EPartnerMessage.STATUS, this.tcpIP, this.tcpPort, this.partner.size(), this.uuid).getNetworkString();
        this.client.publishMsg(msg);
    }

    public void requestSocketData() {
        // TODO publish connection request
        String msg = new PartnerMessage(EPartnerMessage.REQUEST, null, 0, 0, this.uuid).getNetworkString();
        this.client.publishMsg(msg);
    }

    public void receivePublishedInformation() {
        new Thread(() -> {
            while (this.running) {
                String receivedString = this.server.receive();
                PartnerMessage msg = new PartnerMessage(receivedString);
                if (msg.getMsgType().equals(EPartnerMessage.STATUS)) {
                    this.addNewPartner(msg);
                    this.checkAmount(msg);
                } else if (msg.getMsgType().equals(EPartnerMessage.REQUEST)) {
                    this.publishStatus();
                }
            }
        }).start();
    }

    public void addNewPartner(PartnerMessage msg) {
        if (msg == null) throw new IllegalArgumentException();
        if (!this.partner.containsKey(msg.getUuid())) {
            this.partner.put(msg.getUuid(), new NetworkTuple(msg.getIp(), msg.getPort()));
        }
    }

    public void checkAmount(PartnerMessage msg) {
        if (msg == null) throw new IllegalArgumentException();
        if (msg.getAmount() < this.partner.size()) {
            // TODO send I got more
            logger.info(() -> "I got [" + this.partner.size() + "/" + msg.getAmount() + "] partner");
            this.publishStatus();
        } else if (msg.getAmount() == this.partner.size()) {
            // TODO send I got same
            logger.info(() -> "I got [" + this.partner.size() + "/" + msg.getAmount() + "] partner");
        } else if (msg.getAmount() > this.partner.size()) {
            // TODO send I got less -> need more
            logger.info(() -> "I got [" + this.partner.size() + "/" + msg.getAmount() + "] partner");
            this.requestSocketData();
            this.publishStatus();
        }
    }


}
