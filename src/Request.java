import Config.ConfigFile;

import java.util.UUID;

public class Request implements Comparable<Request> {
    private final Integer clock;
    private final UUID procID;
    private final MsgEnum msgType;

    public Request(int clock, UUID procID, MsgEnum msgType) {
        this.clock = clock;
        this.procID = procID;
        this.msgType = msgType;
    }

    public Request(String request) {
        if (request == null) throw new IllegalArgumentException();
        String[] tokens = request.split(ConfigFile.SEPERATOR_REGEX);
        this.clock = Integer.parseInt(tokens[0]);
        this.procID = UUID.fromString(tokens[1]);
        this.msgType = MsgEnum.valueOf(tokens[2]);
    }

    public int getClock() {
        return clock;
    }

    public MsgEnum getMsgType() {
        return msgType;
    }

    public UUID getProcID() {
        return procID;
    }

    public String getNetworkString() {
        String result = "";
        result += this.clock.toString();
        result += ConfigFile.SEPERATOR_CONCAT;
        result += this.procID.toString();
        result += ConfigFile.SEPERATOR_CONCAT;
        result += this.msgType.toString();
        return result;
    }

    @Override
    public int compareTo(Request request) {
        if (request == null) throw new IllegalArgumentException();
        if (Integer.compare(getClock(), request.getClock()) == 0) {
            return this.procID.compareTo(request.getProcID());
        } else {
            return Integer.compare(getClock(), request.getClock());
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "clock=" + clock +
                ", procID=" + procID.toString() +
                ", msgType=" + msgType.toString() +
                '}';
    }
}
