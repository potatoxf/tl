package pxf.tl.annotation;

import java.lang.annotation.*;

/**
 * 静态域值，用于通一个父类下的不同的子类属性域值
 *
 * @author potatoxf
 * @see potatoxf.helper.lang.StaticFieldExtractor
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StaticField {
    /**
     * 静态域的名字
     *
     * @return 返回字符串值
     */
    String[] name() default {};

    /**
     * 静态域的值
     *
     * @return 返回字符串值
     */
    String[] value() default {};
}
