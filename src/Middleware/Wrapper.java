package Middleware;

public class Wrapper {
    private String methodName;
    private Object [] objectParams;
    private Class<?> [] classParams;
    private String className;

    public String getMethodName() {
        return methodName;
    }

    public Object[] getObjectParams() {
        return objectParams;
    }

    public Class<?>[] getClassParams() {
        return classParams;
    }

    public String getClassName() {
        return className;
    }

    public Wrapper(String methodName, Object[] objectParams, Class<?>[] classParams, String className) {
        this.methodName = methodName;
        this.objectParams = objectParams;
        this.classParams = classParams;
        this.className = className;
    }
}
