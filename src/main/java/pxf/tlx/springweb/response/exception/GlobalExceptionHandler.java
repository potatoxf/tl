package pxf.tlx.springweb.response.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pxf.tlx.springweb.response.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 全局异常处理器
 *
 * @author potatoxf
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final GlobalExceptionResponseResultHandler globalExceptionResponseResultHandler;

    public GlobalExceptionHandler(GlobalExceptionResponseResultHandler globalExceptionResponseResultHandler) {
        this.globalExceptionResponseResultHandler = Objects.requireNonNull(globalExceptionResponseResultHandler);
    }

    /**
     * 处理异常
     *
     * @param request   请求
     * @param exception 异常
     * @return {@code ResponseResult<?>}
     */
    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public ResponseResult<?> handleException(HttpServletRequest request, Throwable exception) {
        return globalExceptionResponseResultHandler.handleException(request, exception);
    }
}
