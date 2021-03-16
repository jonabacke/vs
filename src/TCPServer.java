import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private ServerSocket serverSocket;
    private Socket socket;
    private String ip;
    private int port;

    public TCPServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void initServer() {
        try {
            this.serverSocket = new ServerSocket();
            this.socket = this.serverSocket.accept(); // TODO threadpool
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String reiciveMessages() {
        String result = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            result = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

}
