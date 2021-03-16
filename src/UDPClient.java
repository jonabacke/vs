import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class UDPClient {
    private static final Logger logger = Logger.getGlobal();
    
    private InetAddress address;
    private DatagramSocket socket;
    private byte[] buf;
    private final Map<UUID, Integer> robos;

    public UDPClient() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.socket.close()));
        this.robos = new HashMap<>();
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        try {
            this.address = InetAddress.getByName(UDPServer.MULTICAST_INET_ADDRESS);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void sendTo(List<UUID> ids, Request request) {
        this.buf = request.getNetworkString().getBytes();
        if (buf.length > 256) {
            logger.severe("msg to long");
        }

        ids.forEach(robo -> {
            if (!this.robos.containsKey(robo)) {
                logger.warning(() -> "ID [" + robo + "] is not in Map but in List");
            } else {
                send(this.buf, this.buf.length, this.address, this.robos.get(robo), this.socket);
            }

        });
    }

    public static void send(byte[] buf, int length, InetAddress address, int port, DatagramSocket socket) {
        DatagramPacket packet = new DatagramPacket(buf, length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRobo(UUID id, Integer port) {
        this.robos.put(id, port);
    }
}
