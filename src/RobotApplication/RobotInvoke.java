package RobotApplication;

import Config.ConfigFile;
import Middleware.Middleware;

public class RobotInvoke {

    private final Middleware middleware;

    public RobotInvoke(Middleware middleware) {
        this.middleware = middleware;
    }

    public void register(String uuid) {
        this.middleware.invoke(ConfigFile.REGISTER, uuid);
    }

    public void welding() {
        this.middleware.invoke(ConfigFile.WELDING, null);
    }

    public void notWelding() {
        this.middleware.invoke(ConfigFile.NOT_WELDING, null);
    }
}
