package FindPartner;

import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

public class MulticastServer {
    private static final Logger logger = Logger.getGlobal();

    private MulticastSocket socket = null;
    private final byte[] buffer = new byte[256];
    InetSocketAddress group = null;
    NetworkInterface networkInterface;

    public MulticastServer() {
        this.initSocket();
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeSocket));
    }

    private void initSocket() {
        try {
            this.socket = new MulticastSocket(9000);
            group = new InetSocketAddress(InetAddress.getByName("239.0.0.1"), 9000);
            // NetworkInterface.networkInterfaces().forEach(x -> logger.info(()-> "interface: " + x.getName()));
            this.networkInterface = NetworkInterface.getByName("ConfigFile.NETWORK_INTERFACE");
            this.socket.joinGroup(group, this.networkInterface);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive() {
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);
        try {
            //this.socket.send(new DatagramPacket(this.buffer, this.buffer.length, this.group));
            this.socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(packet.getData(), 0, packet.getLength());
    }

    public void closeSocket() {
        try {
            this.socket.leaveGroup(group, this.networkInterface);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.socket.close();
    }
}
