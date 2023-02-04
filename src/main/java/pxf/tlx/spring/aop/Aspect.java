package pxf.tlx.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author potatoxf
 */
public interface Aspect {

    default void before(JoinPoint joinPoint) throws Throwable {
    }

    default void after(JoinPoint joinPoint) throws Throwable {
    }

    default void afterThrowing(JoinPoint joinPoint, Throwable ex) throws Throwable {
    }

    default void afterReturning(JoinPoint joinPoint, Object ret) throws Throwable {
    }

    default Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return proceedingJoinPoint.proceed();
    }
}
