package Middleware;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class SkeletonStub implements IMiddlewareCallableStub {
    private final Object object;

    public SkeletonStub(String serviceName, Object object, IMiddlewareRegisterService middleware, boolean isReliable) {
        this.object = object;
        middleware.register(serviceName, this, isReliable);
    }

    @Override
    public void call(String methodName, Object [] objectParams, Class<?> [] classParams) {
        Logger.getGlobal().fine(this.object.getClass().getName());
        Logger.getGlobal().fine(methodName);
        Logger.getGlobal().fine(objectParams[0].toString());
        try {
            Method method = this.object.getClass().getMethod(methodName, classParams);
            method.invoke(this.object, objectParams);
        } catch (NoSuchMethodException e) {
            Logger.getGlobal().severe("NoSuchMethodException: " + e.getMessage());
        } catch (IllegalAccessException e) {
            Logger.getGlobal().severe("IllegalAccessException: " + e.getMessage());
        } catch (InvocationTargetException e) {
            Logger.getGlobal().severe("InvocationTargetException: " + e.getCause().getMessage());
        }
    }
}
