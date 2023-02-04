package pxf.tlx.spring.web.response;

import java.lang.annotation.*;

/**
 * 配置模板根路径
 *
 * @author potatoxf
 * @see ResponseViewHandler
 */
@Documented
@Target({ElementType.TYPE, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseViewRootPath {

    /**
     * 配置模板根路径，格式{@code a/b/c}
     * <p>
     * 注意这个的开始横杠有没有都没关系，会统一添加或删除
     *
     * @return 返回模板根路径
     * @see ResponseViewHandler
     */
    String value() default "";
}
