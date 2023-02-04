package pxf.tlx.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import pxf.tl.api.InstanceSupplier;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author potatoxf
 */
public abstract class TailInWorkAspect implements Aspect {
    private static final InstanceSupplier<Executor> EXECUTOR = InstanceSupplier.of(Executors::newCachedThreadPool);
    private final Map<String, TailInWork> tailInWorks = new ConcurrentSkipListMap<>();

    public void addTailInWork(@Nonnull String key, @Nonnull TailInWork tailInWork) {
        if (tailInWork != null) {
            tailInWorks.put(key, tailInWork);
        }
    }

    @Override
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Throwable throwable = null;
        Object result = null;
        try {
            result = Aspect.super.around(proceedingJoinPoint);
            return result;
        } catch (Throwable e) {
            throwable = e;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            if (tailInWorks != null) {
                for (String key : tailInWorks.keySet()) {
                    TailInWork tailInWork = tailInWorks.get(key);
                    Object finalResult = result;
                    Throwable finalThrowable = throwable;
                    EXECUTOR.get().execute(() -> tailInWork.execute(startTime - endTime, proceedingJoinPoint, finalResult, finalThrowable));
                }
            }
        }
    }
}
