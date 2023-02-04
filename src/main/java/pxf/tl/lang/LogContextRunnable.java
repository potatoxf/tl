package pxf.tl.lang;

import org.slf4j.MDC;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * 封装日志环境的{@code Runnable}接口，装饰者模式
 *
 * @author potatoxf
 */
public class LogContextRunnable implements Runnable {
    private final Runnable target;
    private final Map<String, String> map;

    /**
     * 传入{@code Runnable}类，并获取主线程中的所有参数
     *
     * @param target
     */
    public LogContextRunnable(@Nonnull Runnable target) {
        this.target = target;
        this.map = MDC.getCopyOfContextMap();
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread,
     * starting the thread causes the object's <code>run</code> method to be called in that separately
     * executing thread.
     *
     * <p>The general contract of the method <code>run</code> is that it may take any action
     * whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        // 可能是null，必须判断，追踪进代码可见null时会发生NPE
        if (map != null) {
            MDC.setContextMap(map);
        }
        try {
            target.run();
        } finally {
            // 即使未执行setContextMap，这里可以clear不出错
            MDC.clear();
        }
    }
}
