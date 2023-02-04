package pxf.tlx.spring.web.request;

import java.lang.annotation.*;

/**
 * 请求数据接口注解
 *
 * @author potatoxf
 * @see RequestDataMethodArgumentResolver
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestData {
}
