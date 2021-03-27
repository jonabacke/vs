package Middleware;

import java.util.UUID;

public interface IMiddlewareInvoke {
    public void invoke(UUID partnerUUID, Class<?> className, String function, Object [] values, boolean isReliable);
}
