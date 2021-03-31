package Middleware;

import Config.ConfigFile;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class Marshaller {
    public static Wrapper unpack(String marshalledString) {
        Logger.getGlobal().fine("marshalledString: " + marshalledString);
        StringTokenizer tokenizer = new StringTokenizer(marshalledString, ConfigFile.SEPARATOR_NETWORK_CONCAT);
        Wrapper wrapper = null;
        int amountArguments = (tokenizer.countTokens() - 2) / 2;
        String serviceName = tokenizer.nextToken();
        String methodName = tokenizer.nextToken();
        Object[] arguments = new Object[amountArguments];
        Class<?> serviceClass = null;

        try {
            serviceClass = Class.forName(serviceName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        assert serviceClass != null;
        for (Method method : serviceClass.getDeclaredMethods()) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                Parameter[] parameters = method.getParameters();
                if (parameters.length != amountArguments) {
                    throw new IllegalArgumentException("Wrong amount of parameter");
                }
                castParameters(tokenizer, arguments, parameters);
                wrapper = new Wrapper(methodName, arguments, method.getParameterTypes(), serviceName);
                break;
            }
        }
        if (wrapper == null) throw new IllegalArgumentException();
        return wrapper;
    }

    public static String pack(Class<?> serviceClass, String function, Object[] parameterValues) {
        String result = "";
        result += serviceClass.getName();
        result += ConfigFile.SEPARATOR_NETWORK_CONCAT;
        for (Method method : serviceClass.getDeclaredMethods()) {
            if (method.getName().equalsIgnoreCase(function)) {
                result = result.concat(method.getName());
                result += ConfigFile.SEPARATOR_NETWORK_CONCAT;
                Parameter[] parameters = method.getParameters();
                if (parameterValues.length != parameters.length) {
                    throw new IllegalArgumentException();
                }
                for (int i = 0; i < parameters.length; i++) {
                    String type = parameters[i].getType().getSimpleName();
                    if (parameterValues[i] == null) {
                        throw new IllegalArgumentException();
                    }
                    if (type.equalsIgnoreCase(parameterValues[i].getClass().getSimpleName())) {
                        result = result.concat(type);
                        result += ConfigFile.SEPARATOR_NETWORK_CONCAT;
                    } else if (parameters[i].getType().getName().equals("int")) {
                        result = result.concat("Integer");
                        result += ConfigFile.SEPARATOR_NETWORK_CONCAT;
                    } else {
                        throw new IllegalArgumentException();
                    }
                    result = result.concat(parameterValues[i].toString());
                    result += ConfigFile.SEPARATOR_NETWORK_CONCAT;
                }
            }
        }
        Logger.getGlobal().fine(result);
        return result;
    }

    public static void castParameters(StringTokenizer tokenizer, Object[] arguments, Parameter[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            String type = parameters[i].getType().getSimpleName();
            String paramType = tokenizer.nextToken();
            String paramValue = tokenizer.nextToken();
            if (type.equalsIgnoreCase(paramType) || type.equalsIgnoreCase("int")) {
                if (parameters[i].getType().isAssignableFrom(Integer.class) || parameters[i].getType().getName().equalsIgnoreCase("int")) {
                    arguments[i] = Integer.parseInt(paramValue);
                } else {
                    arguments[i] = parameters[i].getType().cast(paramValue);
                }
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
}
