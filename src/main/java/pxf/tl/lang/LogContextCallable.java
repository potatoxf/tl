package pxf.tl.lang;

import org.slf4j.MDC;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 封装日志环境的{@code Callable}接口，装饰者模式
 *
 * @author potatoxf
 */
public class LogContextCallable<V> implements Callable<V> {
    private final Callable<V> target;
    private final Map<String, String> map;

    /**
     * 传入{@code Callable}类，并获取主线程中的所有参数
     *
     * @param target
     */
    public LogContextCallable(@Nonnull Callable<V> target) {
        this.target = target;
        this.map = MDC.getCopyOfContextMap();
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public V call() throws Exception {
        // 可能是null，必须判断，追踪进代码可见null时会发生NPE
        if (map != null) {
            MDC.setContextMap(map);
        }
        try {
            return target.call();
        } finally {
            // 即使未执行setContextMap，这里可以clear不出错
            MDC.clear();
        }
    }
}
