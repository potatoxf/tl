package pxf.tlx.spring.web.response.exception;


import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 *
 * @author potatoxf
 */
public interface GlobalExceptionPageHandler {

    /**
     * 处理请求发生的异常并返回相应的页面路径
     *
     * @param request   请求
     * @param exception 异常
     * @return {@code String}
     */
    @Nonnull
    String handleException(@Nonnull HttpServletRequest request, @Nonnull Throwable exception);
}
