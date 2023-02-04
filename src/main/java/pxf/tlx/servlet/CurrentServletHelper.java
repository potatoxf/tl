package pxf.tlx.servlet;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 当前Servlet环境
 * <p>
 * {@link ServletRequestAttributes}
 *
 * @author potatoxf
 */
public final class CurrentServletHelper {

    public static HttpServletRequest getHttpServletRequest() {
        return getServletRequestAttributes().getRequest();
    }

    public static HttpServletResponse getHttpServletResponse() {
        return getServletRequestAttributes().getResponse();
    }

    public static String getSessionId() {
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }

    public static Object getSessionMutex() {
        return RequestContextHolder.currentRequestAttributes().getSessionMutex();
    }

    public static Object getAttributeNames(int scope) {
        return RequestContextHolder.currentRequestAttributes().getAttributeNames(scope);
    }

    public static Object getAttribute(String name, int scope) {
        return RequestContextHolder.currentRequestAttributes().getAttribute(name, scope);
    }

    public static void getAttribute(String name, Object value, int scope) {
        RequestContextHolder.currentRequestAttributes().setAttribute(name, value, scope);
    }

    public static void removeAttribute(String name, int scope) {
        RequestContextHolder.currentRequestAttributes().removeAttribute(name, scope);
    }

    public static Object resolveReference(String key) {
        return RequestContextHolder.currentRequestAttributes().resolveReference(key);
    }

    public static void registerDestructionCallback(String name, Runnable callback, int scope) {
        RequestContextHolder.currentRequestAttributes().registerDestructionCallback(name, callback, scope);
    }

    public static ServletRequestAttributes getServletRequestAttributes() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes;
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
