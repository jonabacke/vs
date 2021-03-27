package Lamport;

import Config.ConfigFile;
import Middleware.Middleware;

import java.util.UUID;

public class LamportInvoke {

    Middleware middleware;

    public LamportInvoke(Middleware middleware) {
        this.middleware = middleware;
    }

    public void send(UUID target, String request) {
        this.middleware.invoke(target, LamportMutex.class, ConfigFile.RECEIVE, new String[]{request}, true);
    }
}
