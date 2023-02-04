package pxf.tlx.spring.web.response.exception;


import pxf.tlx.basesystem.ResponseResult;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 *
 * @author potatoxf
 */
public interface GlobalExceptionBodyHandler {

    /**
     * 处理请求发生的异常并返回相应的结果
     *
     * @param request   请求
     * @param exception 异常
     * @return {@code ApiResult<?>}
     */
    @Nonnull
    ResponseResult<?> handleException(@Nonnull HttpServletRequest request, @Nonnull Throwable exception);
}
