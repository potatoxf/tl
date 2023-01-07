package pxf.tl.lang;


import pxf.tl.api.PoolOfArray;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * 抽象Java动态代理处理类 {@code InvocationHandler}
 *
 * @author potatoxf
 */
public abstract class AbstractInvocationHandler implements InvocationHandler {

    private static final String HASH_CODE = "hashCode";
    private static final String TO_STRING = "toString";
    private static final String EQUALS = "equals";
    protected final Object origin;

    public AbstractInvocationHandler(Object origin) {
        this.origin = origin;
    }

    private static boolean isProxyOfSameInterfaces(Object arg, Class<?> proxyClass) {
        return proxyClass.isInstance(arg)
                || Proxy.isProxyClass(arg.getClass())
                && Arrays.equals(arg.getClass().getInterfaces(), proxyClass.getInterfaces());
    }

    @Override
    public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args == null) {
            args = PoolOfArray.EMPTY_OBJECT_ARRAY;
        }
        if (args.length == 0 && HASH_CODE.equals(method.getName())) {
            return this.hashCode();
        }

        if (args.length == 1
                && EQUALS.equals(method.getName())
                && method.getParameterTypes()[0] == Object.class) {
            Object arg = args[0];
            if (arg == null) {
                return false;
            } else {
                return proxy == arg
                        || isProxyOfSameInterfaces(arg, proxy.getClass())
                        && this.equals(Proxy.getInvocationHandler(arg));
            }
        }
        if (args.length == 0 && TO_STRING.equals(method.getName())) {
            return this.toString();
        }
        if (isNoProxyMethod(method, args)) {
            return method.invoke(origin);
        }
        Throwable throwable = throwExceptionMethod(method, args);
        if (throwable != null) {
            throw throwable;
        }
        return doInvoke(proxy, method, args);
    }

    protected boolean isNoProxyMethod(Method method, Object[] args) {
        return false;
    }

    /**
     * 抛出异常方法，如果有异常返回则会抛出
     *
     * @param method 方法
     * @param args   参数
     * @return 返回异常
     */
    protected Throwable throwExceptionMethod(Method method, Object[] args) {
        return null;
    }

    /**
     * 处理方法
     *
     * @param proxy  代理对象
     * @param method 方法
     * @param args   参数
     * @return 返回方法的返回值
     * @throws Throwable 如果发生异常
     */
    protected abstract Object doInvoke(Object proxy, Method method, Object[] args) throws Throwable;

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
