package pxf.tlx.spring.web.response.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 全局异常处理器
 *
 * @author potatoxf
 */
@ControllerAdvice
public class GlobalExceptionPageAdvice {

    private final GlobalExceptionPageHandler globalExceptionPageHandler;

    public GlobalExceptionPageAdvice(GlobalExceptionPageHandler globalExceptionPageHandler) {
        this.globalExceptionPageHandler = Objects.requireNonNull(globalExceptionPageHandler);
    }

    /**
     * 处理异常
     *
     * @param request   请求
     * @param exception 异常
     * @return {@code String}
     */
    @ExceptionHandler(value = Throwable.class)
    public String handleException(HttpServletRequest request, Throwable exception) {
        return globalExceptionPageHandler.handleException(request, exception);
    }
}
