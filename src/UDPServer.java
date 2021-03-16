import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

public class UDPServer {
    private static final Logger logger = Logger.getGlobal();

    private static final int SERVICE_PORT = 9000;
    public static final String  MULTICAST_INET_ADDRESS = "230.0.0.30";
    private MulticastSocket socket;
    private final byte[] buf;

    public UDPServer() {
        this.buf = new byte[256];
        for (int i = 0; i < 100; i++) {
            try {
                this.socket = new MulticastSocket(SERVICE_PORT + i);
                InetSocketAddress group = new InetSocketAddress(MULTICAST_INET_ADDRESS, SERVICE_PORT + i);
                this.socket.joinGroup(group, null);
            } catch (IOException ignored) {
            }
        }
    }

    public Request receive() {
        Request result;
        DatagramPacket packet = new DatagramPacket(this.buf, this.buf.length);
        try {
            this.socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String received = new String(packet.getData(), 0, packet.getLength());
        logger.info(() -> "Received String: [" + received + "]");
        result = new Request(received);
        return result;
    }
}
