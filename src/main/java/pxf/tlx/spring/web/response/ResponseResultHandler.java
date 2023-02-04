package pxf.tlx.spring.web.response;

import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import pxf.tl.api.Literal;
import pxf.tlx.basesystem.ResponseResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link ResponseResult}返回指定的数据结构
 *
 * @author potatoxf
 */
@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class ResponseResultHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            @Nonnull MethodParameter methodParameter,
            @Nonnull Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.hasMethodAnnotation(ResponseBody.class)
                || methodParameter.getDeclaringClass().getAnnotation(RestController.class) != null;
    }

    @Override
    public Object beforeBodyWrite(
            @Nullable Object body,
            @Nonnull MethodParameter returnType,
            @Nonnull MediaType selectedContentType,
            @Nonnull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @Nonnull ServerHttpRequest request,
            @Nonnull ServerHttpResponse response) {
        if (body instanceof ResponseResult<?>) {
            return body;
        }
        if (body instanceof Literal<?> literal) {
            if (literal instanceof Throwable) {
                return ResponseResult.ofFail(literal);
            } else {
                return ResponseResult.ofSuccess(literal, null);
            }
        }
        if (body instanceof ResponseResultSupplier responseResultSupplier) {
            return responseResultSupplier.getResponseResult();
        }
        ResponseInfo responseInfo = returnType.getMethodAnnotation(ResponseInfo.class);
        if (responseInfo != null) {
            if (body == null) {
                return ResponseResult.ofSuccess(responseInfo.code(), responseInfo.message(), null);
            } else {
                if (body instanceof Boolean) {
                    return ResponseResult.of((Boolean) body, responseInfo.code(), responseInfo.message(), null);
                }
                if (body instanceof HttpStatus) {
                    return ResponseResult.of((HttpStatus) body, responseInfo.message(), null);
                }
            }
            return ResponseResult.ofSuccess(responseInfo.code(), responseInfo.message(), body);
        } else {
            if (body == null) {
                return ResponseResult.ofSuccess();
            } else {
                if (body instanceof Boolean) {
                    return ResponseResult.of((Boolean) body);
                }
                if (body instanceof HttpStatus) {
                    return ResponseResult.of((HttpStatus) body);
                }
            }
            return ResponseResult.ofSuccess(body);
        }
    }
}
