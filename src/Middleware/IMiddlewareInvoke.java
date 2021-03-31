package Middleware;

import java.util.UUID;

public interface IMiddlewareInvoke {
    void invoke(UUID partnerUUID, Class<?> className, String function, Object[] values, boolean isReliable);
}
