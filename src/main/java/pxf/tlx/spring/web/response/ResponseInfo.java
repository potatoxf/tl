package pxf.tlx.spring.web.response;

import pxf.tlx.basesystem.ResponseResultStatus;

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
    int code() default ResponseResultStatus.CODE_OK;

    /**
     * @return 处理信息
     */
    String message() default ResponseResultStatus.MESSAGE_OK;
}
