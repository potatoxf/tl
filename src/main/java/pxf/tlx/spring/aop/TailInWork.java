package pxf.tlx.spring.aop;

import org.aspectj.lang.JoinPoint;

/**
 * @author potatoxf
 */
public interface TailInWork {

    /**
     * @param executeTime
     * @param joinPoint
     * @param result
     * @param throwable
     */
    void execute(long executeTime, JoinPoint joinPoint, Object result, Throwable throwable);
}
