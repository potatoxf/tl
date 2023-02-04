package pxf.tlx.mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author potatoxf
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface AutoFill {

    /**
     * 返回填充类型
     *
     * @return {@code Class<AutoFillValueHandler>[]}
     */
    Class<? extends AutoFillValueHandler>[] value() default {};

    /**
     * 是否插入时执行
     *
     * @return 如果是返回true，否则返回false
     */
    boolean isInsert() default true;

    /**
     * 是否更新时候执行
     *
     * @return 如果是返回true，否则返回false
     */
    boolean isUpdate() default true;
}
