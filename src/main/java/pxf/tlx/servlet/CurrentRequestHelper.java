package pxf.tlx.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * 当前请求助手
 * <p>
 * {@link HttpServletRequest}
 *
 * @author potatoxf
 */
public final class CurrentRequestHelper {
    public static String getAuthType() {
        return CurrentServletHelper.getHttpServletRequest().getAuthType();
    }

    public static Cookie[] getCookies() {
        return CurrentServletHelper.getHttpServletRequest().getCookies();
    }

    public static long getDateHeader(String name) {
        return CurrentServletHelper.getHttpServletRequest().getDateHeader(name);
    }

    public static String getHeader(String name) {
        return CurrentServletHelper.getHttpServletRequest().getHeader(name);
    }

    public static Enumeration<String> getHeaders(String name) {
        return CurrentServletHelper.getHttpServletRequest().getHeaders(name);
    }

    public static Enumeration<String> getHeaderNames() {
        return CurrentServletHelper.getHttpServletRequest().getHeaderNames();
    }

    public static int getIntHeader(String name) {
        return CurrentServletHelper.getHttpServletRequest().getIntHeader(name);
    }

    public static String getMethod() {
        return CurrentServletHelper.getHttpServletRequest().getMethod();
    }

    public static String getPathInfo() {
        return CurrentServletHelper.getHttpServletRequest().getPathInfo();
    }

    public static String getPathTranslated() {
        return CurrentServletHelper.getHttpServletRequest().getPathTranslated();
    }

    public static String getContextPath() {
        return CurrentServletHelper.getHttpServletRequest().getContextPath();
    }

    public static String getQueryString() {
        return CurrentServletHelper.getHttpServletRequest().getQueryString();
    }

    public static String getRemoteUser() {
        return CurrentServletHelper.getHttpServletRequest().getRemoteUser();
    }

    public static boolean isUserInRole(String role) {
        return CurrentServletHelper.getHttpServletRequest().isUserInRole(role);
    }

    public static Principal getUserPrincipal() {
        return CurrentServletHelper.getHttpServletRequest().getUserPrincipal();
    }

    public static String getRequestedSessionId() {
        return CurrentServletHelper.getHttpServletRequest().getRequestedSessionId();
    }

    public static String getRequestURI() {
        return CurrentServletHelper.getHttpServletRequest().getRequestURI();
    }

    public static StringBuffer getRequestURL() {
        return CurrentServletHelper.getHttpServletRequest().getRequestURL();
    }

    public static String getServletPath() {
        return CurrentServletHelper.getHttpServletRequest().getServletPath();
    }

    public static HttpSession getSession(boolean create) {
        return CurrentServletHelper.getHttpServletRequest().getSession(create);
    }

    public static HttpSession getSession() {
        return CurrentServletHelper.getHttpServletRequest().getSession();
    }

    public static String changeSessionId() {
        return CurrentServletHelper.getHttpServletRequest().changeSessionId();
    }

    public static boolean isRequestedSessionIdValid() {
        return CurrentServletHelper.getHttpServletRequest().isRequestedSessionIdValid();
    }

    public static boolean isRequestedSessionIdFromCookie() {
        return CurrentServletHelper.getHttpServletRequest().isRequestedSessionIdFromCookie();
    }

    public static boolean isRequestedSessionIdFromURL() {
        return CurrentServletHelper.getHttpServletRequest().isRequestedSessionIdFromURL();
    }

    public static boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return CurrentServletHelper.getHttpServletRequest().authenticate(response);
    }

    public static void login(String username, String password) throws ServletException {
        CurrentServletHelper.getHttpServletRequest().login(username, password);
    }

    public static void logout() throws ServletException {
        CurrentServletHelper.getHttpServletRequest().logout();
    }

    public static Collection<Part> getParts() throws IOException, ServletException {
        return CurrentServletHelper.getHttpServletRequest().getParts();
    }

    public static Part getPart(String name) throws IOException, ServletException {
        return CurrentServletHelper.getHttpServletRequest().getPart(name);
    }

    public static <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return CurrentServletHelper.getHttpServletRequest().upgrade(handlerClass);
    }

    public static HttpServletMapping getHttpServletMapping() {
        return CurrentServletHelper.getHttpServletRequest().getHttpServletMapping();
    }

    public static PushBuilder newPushBuilder() {
        return CurrentServletHelper.getHttpServletRequest().newPushBuilder();
    }

    public static Map<String, String> getTrailerFields() {
        return CurrentServletHelper.getHttpServletRequest().getTrailerFields();
    }

    public static boolean isTrailerFieldsReady() {
        return CurrentServletHelper.getHttpServletRequest().isTrailerFieldsReady();
    }

    public static Object getAttribute(String name) {
        return CurrentServletHelper.getHttpServletRequest().getAttribute(name);
    }

    public static Enumeration<String> getAttributeNames() {
        return CurrentServletHelper.getHttpServletRequest().getAttributeNames();
    }

    public static String getCharacterEncoding() {
        return CurrentServletHelper.getHttpServletRequest().getCharacterEncoding();
    }

    public static void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        CurrentServletHelper.getHttpServletRequest().setCharacterEncoding(env);
    }

    public static int getContentLength() {
        return CurrentServletHelper.getHttpServletRequest().getContentLength();
    }

    public static long getContentLengthLong() {
        return CurrentServletHelper.getHttpServletRequest().getContentLengthLong();
    }

    public static String getContentType() {
        return CurrentServletHelper.getHttpServletRequest().getContentType();
    }

    public static ServletInputStream getInputStream() throws IOException {
        return CurrentServletHelper.getHttpServletRequest().getInputStream();
    }

    public static String getParameter(String name) {
        return CurrentServletHelper.getHttpServletRequest().getParameter(name);
    }

    public static Enumeration<String> getParameterNames() {
        return CurrentServletHelper.getHttpServletRequest().getParameterNames();
    }

    public static String[] getParameterValues(String name) {
        return CurrentServletHelper.getHttpServletRequest().getParameterValues(name);
    }

    public static Map<String, String[]> getParameterMap() {
        return CurrentServletHelper.getHttpServletRequest().getParameterMap();
    }

    public static String getProtocol() {
        return CurrentServletHelper.getHttpServletRequest().getProtocol();
    }

    public static String getScheme() {
        return CurrentServletHelper.getHttpServletRequest().getScheme();
    }

    public static String getServerName() {
        return CurrentServletHelper.getHttpServletRequest().getServerName();
    }

    public static int getServerPort() {
        return CurrentServletHelper.getHttpServletRequest().getServerPort();
    }

    public static BufferedReader getReader() throws IOException {
        return CurrentServletHelper.getHttpServletRequest().getReader();
    }

    public static String getRemoteAddr() {
        return CurrentServletHelper.getHttpServletRequest().getRemoteAddr();
    }

    public static String getRemoteHost() {
        return CurrentServletHelper.getHttpServletRequest().getRemoteHost();
    }

    public static void setAttribute(String name, Object value) {
        CurrentServletHelper.getHttpServletRequest().setAttribute(name, value);
    }

    public static void removeAttribute(String name) {
        CurrentServletHelper.getHttpServletRequest().removeAttribute(name);
    }

    public static Locale getLocale() {
        return CurrentServletHelper.getHttpServletRequest().getLocale();
    }

    public static Enumeration<Locale> getLocales() {
        return CurrentServletHelper.getHttpServletRequest().getLocales();
    }

    public static boolean isSecure() {
        return CurrentServletHelper.getHttpServletRequest().isSecure();
    }

    public static RequestDispatcher getRequestDispatcher(String path) {
        return CurrentServletHelper.getHttpServletRequest().getRequestDispatcher(path);
    }

    public static String getRealPath(String path) {
        return CurrentServletHelper.getHttpServletRequest().getServletContext().getRealPath(path);
    }

    public static int getRemotePort() {
        return CurrentServletHelper.getHttpServletRequest().getRemotePort();
    }

    public static String getLocalName() {
        return CurrentServletHelper.getHttpServletRequest().getLocalName();
    }

    public static String getLocalAddr() {
        return CurrentServletHelper.getHttpServletRequest().getLocalAddr();
    }

    public static int getLocalPort() {
        return CurrentServletHelper.getHttpServletRequest().getLocalPort();
    }

    public static ServletContext getServletContext() {
        return CurrentServletHelper.getHttpServletRequest().getServletContext();
    }

    public static AsyncContext startAsync() throws IllegalStateException {
        return CurrentServletHelper.getHttpServletRequest().startAsync();
    }

    public static AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return CurrentServletHelper.getHttpServletRequest().startAsync(servletRequest, servletResponse);
    }

    public static boolean isAsyncStarted() {
        return CurrentServletHelper.getHttpServletRequest().isAsyncStarted();
    }

    public static boolean isAsyncSupported() {
        return CurrentServletHelper.getHttpServletRequest().isAsyncSupported();
    }

    public static AsyncContext getAsyncContext() {
        return CurrentServletHelper.getHttpServletRequest().getAsyncContext();
    }

    public static DispatcherType getDispatcherType() {
        return CurrentServletHelper.getHttpServletRequest().getDispatcherType();
    }
}
