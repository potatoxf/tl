package pxf.tlx.spring.web.response.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import pxf.tlx.basesystem.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 全局异常处理器
 *
 * @author potatoxf
 */
@ControllerAdvice
public class GlobalExceptionBodyAdvice {

    private final GlobalExceptionBodyHandler globalExceptionBodyHandler;

    public GlobalExceptionBodyAdvice(GlobalExceptionBodyHandler globalExceptionBodyHandler) {
        this.globalExceptionBodyHandler = Objects.requireNonNull(globalExceptionBodyHandler);
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
        return globalExceptionBodyHandler.handleException(request, exception);
    }
}
