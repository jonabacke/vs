package Middleware;

public interface IMiddlewareRegisterService {
    public void register(String serviceClass, IMiddlewareCallableStub stub, boolean isReliable);
}
