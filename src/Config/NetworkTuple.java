package Config;

public class NetworkTuple {
    private final String ip;
    private final int port;

    public NetworkTuple(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "NetworkTuple{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
