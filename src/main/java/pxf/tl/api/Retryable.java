package pxf.tl.api;


/**
 * 可重新尝试的
 *
 * @author potatoxf
 */
public interface Retryable extends Refreshable {

    /**
     * 执行动作
     *
     * @throws Throwable for all other error conditions
     */
    void attempt() throws Throwable;

    /**
     * 执行
     *
     * @param interval 间隔
     * @param timeout  超时
     */
    default void execute(long interval, long timeout) {
        execute(0L, interval, timeout);
    }

    /**
     * 执行
     *
     * @param delay    延迟
     * @param interval 间隔
     * @param timeout  超时
     */
    default void execute(long delay, long interval, long timeout) {
        long start = System.currentTimeMillis();
        if (delay > 0L) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        while (true) {
            try {
                attempt();
                return;
            } catch (Throwable e) {
                if (System.currentTimeMillis() - start < timeout) {
                    try {
                        //noinspection BusyWait
                        Thread.sleep(interval);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 将缓存里的内容重刷新
     */
    default void refresh() {
        try {
            attempt();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
