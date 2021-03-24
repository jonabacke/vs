import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class TCPServer {
    private static final Logger logger = Logger.getGlobal();

    private ServerSocket serverSocket;
    BufferedReader bufferedReader;
    private String ip;
    private int port;

    public TCPServer() {
        try {
            this.serverSocket = new ServerSocket(0);
            this.ip = this.serverSocket.getInetAddress().getHostAddress();
            this.port = this.serverSocket.getLocalPort();
            logger.info("IP: " + this.ip + ", Port: " + this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket initServer() {
        Socket socket = null;
            try {
                socket = this.serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return socket;
    }

    public BufferedReader receiveMessages(Socket socket) {
        String result = "";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //result = this.bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("Receive: " + result);
        return bufferedReader;
    }

    public String readLine(BufferedReader bufferedReader) {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeServer() {
        try {
            this.bufferedReader.close();
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
