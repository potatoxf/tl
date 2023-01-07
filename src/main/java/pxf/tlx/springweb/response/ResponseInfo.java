package pxf.tlx.springweb.response;

import java.lang.annotation.*;

/**
 * @author potatoxf
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseInfo {

    /**
     * @return 编码
     */
    int code() default ResponseResult.CODE_OK;

    /**
     * @return 处理信息
     */
    String message() default ResponseResult.MESSAGE_OK;
}
