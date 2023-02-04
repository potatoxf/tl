package pxf.tl.lang.caller;


import java.io.Serial;
import java.io.Serializable;

/**
 * 通过StackTrace方式获取调用者。此方式效率最低，不推荐使用
 *
 * @author potatoxf
 */
public class StackTraceCaller implements Caller, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final int OFFSET = 2;

    @Override
    public Class<?> getCaller() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (OFFSET + 1 >= stackTrace.length) {
            return null;
        }
        final String className = stackTrace[OFFSET + 1].getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("[%s] not found!", className), e);
        }
    }

    @Override
    public Class<?> getCallerCaller() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (OFFSET + 2 >= stackTrace.length) {
            return null;
        }
        final String className = stackTrace[OFFSET + 2].getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("[%s] not found!", className), e);
        }
    }

    @Override
    public Class<?> getCaller(int depth) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (OFFSET + depth >= stackTrace.length) {
            return null;
        }
        final String className = stackTrace[OFFSET + depth].getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("[%s] not found!", className), e);
        }
    }

    @Override
    public boolean isCalledBy(Class<?> clazz) {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (final StackTraceElement element : stackTrace) {
            if (element.getClassName().equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }
}
