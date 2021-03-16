import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class Self implements ILamportMutex {

    private Integer clock;
    private UDPClient channel;
    private Queue<Request> queue;
    private UUID procID;
    private List<UUID> othersProcess;
    private UDPServer udpServer;
    private boolean running;

    public Self() {
        this.init();
    }

    @Override
    public void init() {
        this.clock = 0;
        this.queue = new PriorityQueue<>();
        this.othersProcess = new ArrayList<>();
        this.procID = UUID.randomUUID();
        this.channel = new UDPClient();
        this.udpServer = new UDPServer();
        this.running = false;
    }

    @Override
    public void requestToEnter() {
        this.clock = this.clock + 1;
        this.queue.add(new Request(this.clock, this.procID, MsgEnum.ENTER));
        this.cleanupQ();
        this.channel.sendTo(this.othersProcess, new Request(this.clock, this.procID, MsgEnum.ENTER));
    }

    @Override
    public void allowToEnter(UUID requester) {
        this.clock = this.clock + 1;
        this.channel.sendTo(Collections.singletonList(requester), new Request(this.clock, this.procID, MsgEnum.ALLOW));
    }

    @Override
    public void release() {
        this.queue = this.queue.stream().skip(1).filter(x -> x.getMsgType().equals(MsgEnum.ENTER))
                .collect(Collectors.toCollection(ConcurrentLinkedQueue::new));
        this.clock = this.clock + 1;
        this.channel.sendTo(this.othersProcess, new Request(this.clock, this.procID, MsgEnum.RELEASE));
    }

    @Override
    public boolean allowedToEnter() {
        long commProcess = this.queue.stream().skip(1).filter(x -> x.getMsgType().equals(MsgEnum.ALLOW)).map(Request::getProcID).count();
        assert this.queue.peek() != null;
        return this.queue.peek().getProcID().equals(this.procID) && this.othersProcess.size() == commProcess;
    }

    @Override
    public void receive() {
        this.running = true;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.running = false));

        new Thread(() -> {
            Request msg;
            while (this.running) {
                msg = udpServer.receive();
                this.clock = Math.max(this.clock, msg.getClock());
                this.clock = this.clock + 1;
                switch (msg.getMsgType()) {
                case ENTER:
                    this.queue.add(msg);
                    this.allowToEnter(msg.getProcID());
                    break;
                case ALLOW:
                    this.queue.add(msg);
                    break;
                case RELEASE:
                    this.queue.remove();
                    break;
                case REGISTER:
                    this.othersProcess.add(msg.getProcID());
                    break;
                default:
                    break;
                }
                this.cleanupQ();
            }
        }).start();
    }

    private void cleanupQ() {
        this.queue.removeIf(x -> x.getMsgType().equals(MsgEnum.ALLOW) && x.getClock() < this.clock);
    }
}
