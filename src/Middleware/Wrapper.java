package Middleware;

public class Wrapper {
    private final String methodName;
    private final Object[] objectParams;
    private final Class<?>[] classParams;
    private final String className;

    public Wrapper(String methodName, Object[] objectParams, Class<?>[] classParams, String className) {
        this.methodName = methodName;
        this.objectParams = objectParams;
        this.classParams = classParams;
        this.className = className;
    }

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
}
