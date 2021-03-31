package Middleware;

public interface IMiddlewareRegisterService {
    void register(String serviceClass, IMiddlewareCallableStub stub, boolean isReliable);
}
