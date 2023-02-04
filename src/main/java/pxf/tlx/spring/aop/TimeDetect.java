package pxf.tlx.spring.aop;

import java.lang.annotation.*;

/**
 * @author potatoxf
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE})
public @interface TimeDetect {
    /**
     * 监控阈值，执行超过这个时间就会触发监听器
     */
    long threshold() default 10000;
}
