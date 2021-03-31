package Middleware;

public interface IMiddlewareCallableStub {
    void call(String methodName, Object[] objectParams, Class<?>[] classParams);
}
