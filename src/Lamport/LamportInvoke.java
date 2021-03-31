package Lamport;

import Config.ConfigFile;
import Middleware.Middleware;

import java.util.UUID;

public class LamportInvoke implements ILamportInvoke {

    Middleware middleware;

    public LamportInvoke(Middleware middleware) {
        this.middleware = middleware;
    }

    @Override
    public void send(UUID target, String request) {
        this.middleware.invoke(target, LamportMutex.class, ConfigFile.RECEIVE, new String[]{request}, true);
    }
}
