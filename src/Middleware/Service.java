package Middleware;

import Config.ConfigFile;
import Lamport.TCPServer;

public class Service {
    private final String serviceName;
    private final boolean isReliable;
    private TCPServer tcpServer;

    public Service(String serviceName, boolean isReliable, TCPServer tcpServer) {
        this.serviceName = serviceName;
        this.isReliable = isReliable;
        this.tcpServer = tcpServer;
    }

    public TCPServer getTcpServer() {
        return tcpServer;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceClassName() {
        return (this.serviceName.split(ConfigFile.SEPARATOR_NETWORK_CONCAT))[0];
    }

    public boolean isReliable() {
        return isReliable;
    }
}
