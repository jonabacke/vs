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
        String[] tokens = request.split("\\|");
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
        result += "|";
        result += this.procID.toString();
        result += "|";
        result += this.msgType.toString();
        result += "|";
        System.out.println(result);
        return result;
    }

    @Override
    public int compareTo(Request request) {
        return Integer.compare(getClock(), request.getClock());
    }
}
