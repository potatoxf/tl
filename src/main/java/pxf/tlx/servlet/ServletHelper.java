package pxf.tlx.servlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet助手
 *
 * @author potatoxf
 */
public final class ServletHelper {

    private static final String UNKNOWN = "unknown";

    /**
     * 获取请求来源的IP,可以适应前置部署有Nginx等反向代理软件等的情况. HTTP_CLIENT_IP 无法伪造，所以放在第一个
     *
     * @param request {@link HttpServletRequest}
     * @return 获取请求来源的IP
     */
    public static String getRequestSourceIp(HttpServletRequest request) {
        String ip = request.getHeader("HTTP_CLIENT_IP");

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED");
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-FORWARDED-FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getCookiePath(HttpServletRequest request) {
        return getCookiePath(request, "/");
    }

    public static String getCookiePath(HttpServletRequest request, String defaultPath) {
        String contextPath = request.getContextPath();
        return contextPath.length() > 0 ? contextPath : defaultPath;
    }

    public static void cancelCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath(getCookiePath(request));
        response.addCookie(cookie);
    }
}
