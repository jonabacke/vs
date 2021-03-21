package FindPartner;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class MulticastClient {
    private static final Logger logger = Logger.getGlobal();

    private DatagramSocket socket = null;
    InetSocketAddress group = null;

    public MulticastClient() {
        this.initSocket();
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeSocket));
    }

    private void initSocket() {
        try {
            this.socket = new DatagramSocket();
            //group = new InetSocketAddress("239.0.0.1", 9000);
            //this.socket.joinGroup(group, NetworkInterface.getByName("lo"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void publishMsg(String msg) {
        if (msg == null) throw new IllegalArgumentException();
        byte[] buffer = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("239.0.0.1"), 9000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        logger.info(()-> "send Msg: " + msg);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
      /**  try {
            this.socket.leaveGroup(group, NetworkInterface.getByName("lo"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        this.socket.close();
    }

}
