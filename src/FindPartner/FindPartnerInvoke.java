package FindPartner;

import Config.ConfigFile;
import Middleware.Middleware;

public class FindPartnerInvoke {

    Middleware middleware;

    public FindPartnerInvoke(Middleware middleware) {
        this.middleware = middleware;
    }

    public void publishMsg(String msg) {
        this.middleware.invoke(null, FindPartner.class, ConfigFile.RECEIVE_PUBLISH_INFORMATION, new String[]{msg}, false);
    }
}
