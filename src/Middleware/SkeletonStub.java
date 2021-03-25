package Middleware;

import Config.ConfigFile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class SkeletonStub implements IMiddlewareCallableStub {
    private Object object;

    public SkeletonStub(String serviceName, Object object, IMiddlewareRegisterService middleware) {
        this.object = object;
        middleware.register(serviceName, this);
    }

    @Override
    public void call(String methodName, Object [] objectParams, Class<?> [] classParams) {
        try {
            Method method = this.object.getClass().getMethod(methodName, classParams);
            method.invoke(this.object, objectParams);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Logger.getGlobal().severe(e.getMessage());
        }
    }
}
