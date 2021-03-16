public class Message {
    int clock;
    MsgEnum msgEnum;

    public Message(int clock, MsgEnum msgEnum) {
        this.clock = clock;
        this.msgEnum = msgEnum;
    }

    public int getClock() {
        return clock;
    }

    public MsgEnum getMsgEnum() {
        return msgEnum;
    }
}
