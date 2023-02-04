package pxf.tl.concurrent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 命名线程工厂
 *
 * @author potatoxf
 */
public class NamedThreadFactory implements ThreadFactory {

    /**
     *
     */
    private static final Map<String, AtomicInteger> NAMED_TOTAL =
            new ConcurrentHashMap<String, AtomicInteger>();
    /**
     *
     */
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    /**
     *
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     *
     */
    private final ThreadGroup group;
    /**
     *
     */
    private final String nameTemplate;
    /**
     *
     */
    private final boolean daemon;
    /**
     * 无法捕获的异常统一处理
     */
    private final Thread.UncaughtExceptionHandler handler;

    /**
     * @param baseName
     * @param handler
     */
    public NamedThreadFactory(String baseName, Thread.UncaughtExceptionHandler handler) {
        this(null, baseName, false, handler);
    }

    /**
     * @param baseName
     * @param daemon
     * @param handler
     */
    public NamedThreadFactory(String baseName, boolean daemon, Thread.UncaughtExceptionHandler handler) {
        this(null, baseName, daemon, handler);
    }

    /**
     * @param group
     * @param baseName
     * @param handler
     */
    public NamedThreadFactory(ThreadGroup group, String baseName, Thread.UncaughtExceptionHandler handler) {
        this(group, baseName, false, handler);
    }

    /**
     * @param group
     * @param baseName
     * @param daemon
     * @param handler
     */
    public NamedThreadFactory(ThreadGroup group, String baseName, boolean daemon, Thread.UncaughtExceptionHandler handler) {
        if (group != null) {
            this.group = group;
        } else {
            this.group = Thread.currentThread().getThreadGroup();
        }
        this.nameTemplate =
                baseName
                        + "-<%d>-pool-<"
                        + POOL_NUMBER.getAndIncrement()
                        + ">-thread="
                        + (daemon ? "D" : "M")
                        + "-<%d>";
        this.daemon = daemon;
        this.handler = handler;
    }

    /**
     * @return
     */
    public static int totalThread() {
        int count = 0;
        Set<String> keySet = NAMED_TOTAL.keySet();
        for (String name : keySet) {
            count += totalThread(name);
        }
        return count;
    }

    /**
     * @param baseName
     * @return
     */
    public static int totalThread(String baseName) {
        AtomicInteger total = NAMED_TOTAL.get(baseName);
        if (total == null) {
            return 0;
        }
        return total.get();
    }

    /**
     * @param baseName
     * @return
     */
    private static int getNameNextIndex(String baseName) {
        AtomicInteger atomicInteger = NAMED_TOTAL.get(baseName);
        if (atomicInteger == null) {
            atomicInteger = new AtomicInteger(1);
            NAMED_TOTAL.put(baseName, atomicInteger);
        }
        return atomicInteger.getAndIncrement();
    }

    /**
     * @param runnable
     * @return
     */
    @Override
    public Thread newThread(Runnable runnable) {
        int namedIndex = getNameNextIndex(nameTemplate);
        Thread thread =
                new Thread(
                        group,
                        runnable,
                        String.format(nameTemplate, namedIndex, threadNumber.getAndIncrement()),
                        0);
        thread.setDaemon(daemon);

        // 异常处理
        if (handler != null) {
            thread.setUncaughtExceptionHandler(handler);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
