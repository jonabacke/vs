package FindPartner;

import java.util.UUID;
import Config.ConfigFile;

public class PartnerMessage {
    // msgType|uuid|ip|port oder msgType|uuid|amount oder msgType
    private EPartnerMessage msgType;
    private int port;
    private String ip;
    private int amount;
    private UUID uuid;

    public PartnerMessage(EPartnerMessage msgType, String ip, int port, int amount, UUID uuid) {
        this.msgType = msgType;
        this.port = port;
        this.ip = ip;
        this.amount = amount;
        this.uuid = uuid;
    }
    public PartnerMessage(String networkString) {
        if (networkString == null) {
            throw new IllegalArgumentException();
        }
        String[] token = networkString.split(ConfigFile.SEPERATOR_REGEX);
        switch (token[0]) {
            case "STATUS" -> {
                if (token.length < 5) throw new IllegalArgumentException();
                this.msgType = EPartnerMessage.STATUS;
                this.uuid = UUID.fromString(token[1]);
                this.ip = token[2];
                this.port = Integer.parseInt(token[3]);
                this.amount = Integer.parseInt(token[4]);
            }
            case "REQUEST" -> {
                if (token.length < 2) throw new IllegalArgumentException();
                this.msgType = EPartnerMessage.REQUEST;
                this.uuid = UUID.fromString(token[1]);
            }
            case "RESET" -> {
                this.msgType = EPartnerMessage.RESET;
            }
        }
    }

    public String getNetworkString() {
        String result = "";
        result += this.msgType;
        result += ConfigFile.SEPERATOR_CONCAT;
        result += this.uuid.toString();
        result += ConfigFile.SEPERATOR_CONCAT;
        if (this.msgType == EPartnerMessage.STATUS) {
            result += this.ip;
            result += ConfigFile.SEPERATOR_CONCAT;
            result += this.port;
            result += ConfigFile.SEPERATOR_CONCAT;
            result += this.amount;
        }
        return result;
    }

    public int getAmount() {
        return amount;
    }

    public EPartnerMessage getMsgType() {
        return msgType;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public UUID getUuid() {
        return uuid;
    }
}
