package pxf.tlx.spring.web.request;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.annotation.Nonnull;
import javax.servlet.ServletRequest;
import java.util.Objects;

/**
 * 请求数据方法参数解析器
 * <p>
 * 解析以下格式
 * application/json
 * application/x-www-form-urlencoded
 * multipart
 *
 * @author potatoxf
 */
public class RequestDataMethodArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     *
     */
    private final RequestResponseBodyMethodProcessor formResolver;
    /**
     * json解析器
     */
    private final ServletModelAttributeMethodProcessor jsonResolver;

    public RequestDataMethodArgumentResolver(RequestResponseBodyMethodProcessor formResolver,
                                             ServletModelAttributeMethodProcessor jsonResolver) {
        this.formResolver = Objects.requireNonNull(formResolver);
        this.jsonResolver = Objects.requireNonNull(jsonResolver);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.getParameterAnnotation(RequestData.class) != null);
    }

    @Override
    public Object resolveArgument(@Nonnull MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        ServletRequest servletRequest = nativeWebRequest.getNativeRequest(ServletRequest.class);
        String contentType = servletRequest.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("不支持contentType");
        }

        if (contentType.contains("application/json")) {
            return jsonResolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
        }

        if (contentType.contains("application/x-www-form-urlencoded")) {
            return formResolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
        }

        if (contentType.contains("multipart")) {
            return formResolver.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
        }

        throw new IllegalArgumentException("不支持contentType");
    }
}