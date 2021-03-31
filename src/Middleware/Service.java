package Middleware;

import Communication.TCPServer;
import Config.ConfigFile;

public class Service {
    private final String serviceName;
    private final boolean isReliable;
    private final TCPServer tcpServer;

    public Service(String serviceName, boolean isReliable, TCPServer tcpServer) {
        this.serviceName = serviceName;
        this.isReliable = isReliable;
        this.tcpServer = tcpServer;
    }

    public TCPServer getTcpServer() {
        return tcpServer;
    }

    public String getServiceClassName() {
        return (this.serviceName.split(ConfigFile.SEPARATOR_NETWORK_CONCAT))[0];
    }

    public boolean isReliable() {
        return isReliable;
    }
}
