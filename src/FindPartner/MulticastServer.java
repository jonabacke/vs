package FindPartner;

import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

public class MulticastServer {
    private static final Logger logger = Logger.getGlobal();

    private MulticastSocket socket = null;
    private final byte[] buffer = new byte[256];
    InetSocketAddress group = null;

    public MulticastServer() {
        this.initSocket();
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeSocket));
    }

    private void initSocket() {
        try {
            this.socket = new MulticastSocket(9000);
            group = new InetSocketAddress("239.0.0.1", 9000);
            this.socket.joinGroup(group, NetworkInterface.getByName("enp3s0"));
            logger.info("ausgeführt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive() {
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);
        try {
            logger.info("davor");
            this.socket.send(new DatagramPacket(this.buffer, this.buffer.length, group));
            this.socket.receive(packet);
            logger.info("danach");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(packet.getData(), 0, packet.getLength());
    }

    public void closeSocket() {
        try {
            this.socket.leaveGroup(group, NetworkInterface.getByName("enp3s0"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.socket.close();
    }
}
