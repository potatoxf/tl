package pxf.tlx.servlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 当前响应助手
 * <p>
 * {@link HttpServletResponse}
 *
 * @author potatoxf
 */
public final class CurrentResponseHelper {

    public static void addCookie(Cookie cookie) {
        CurrentServletHelper.getHttpServletResponse().addCookie(cookie);
    }

    public static boolean containsHeader(String name) {
        return CurrentServletHelper.getHttpServletResponse().containsHeader(name);
    }

    public static String encodeURL(String url) {
        return CurrentServletHelper.getHttpServletResponse().encodeURL(url);
    }

    public static String encodeRedirectURL(String url) {
        return CurrentServletHelper.getHttpServletResponse().encodeRedirectURL(url);
    }

    public static void sendError(int sc, String msg) throws IOException {
        CurrentServletHelper.getHttpServletResponse().sendError(sc, msg);
    }

    public static void sendError(int sc) throws IOException {
        CurrentServletHelper.getHttpServletResponse().sendError(sc);
    }

    public static void sendRedirect(String location) throws IOException {
        CurrentServletHelper.getHttpServletResponse().sendRedirect(location);
    }

    public static void setDateHeader(String name, long date) {
        CurrentServletHelper.getHttpServletResponse().setDateHeader(name, date);
    }

    public static void addDateHeader(String name, long date) {
        CurrentServletHelper.getHttpServletResponse().addDateHeader(name, date);
    }

    public static void setHeader(String name, String value) {
        CurrentServletHelper.getHttpServletResponse().setHeader(name, value);
    }

    public static void addHeader(String name, String value) {
        CurrentServletHelper.getHttpServletResponse().addHeader(name, value);
    }

    public static void setIntHeader(String name, int value) {
        CurrentServletHelper.getHttpServletResponse().setIntHeader(name, value);
    }

    public static void addIntHeader(String name, int value) {
        CurrentServletHelper.getHttpServletResponse().addIntHeader(name, value);
    }

    public static int getStatus() {
        return CurrentServletHelper.getHttpServletResponse().getStatus();
    }

    public static void setStatus(int sc) {
        CurrentServletHelper.getHttpServletResponse().setStatus(sc);
    }

    public static String getHeader(String name) {
        return CurrentServletHelper.getHttpServletResponse().getHeader(name);
    }

    public static Collection<String> getHeaders(String name) {
        return CurrentServletHelper.getHttpServletResponse().getHeaders(name);
    }

    public static Collection<String> getHeaderNames() {
        return CurrentServletHelper.getHttpServletResponse().getHeaderNames();
    }

    public static Supplier<Map<String, String>> getTrailerFields() {
        return CurrentServletHelper.getHttpServletResponse().getTrailerFields();
    }

    public static void setTrailerFields(Supplier<Map<String, String>> supplier) {
        CurrentServletHelper.getHttpServletResponse().setTrailerFields(supplier);
    }

    public static String getCharacterEncoding() {
        return CurrentServletHelper.getHttpServletResponse().getCharacterEncoding();
    }

    public static void setCharacterEncoding(String charset) {
        CurrentServletHelper.getHttpServletResponse().setCharacterEncoding(charset);
    }

    public static String getContentType() {
        return CurrentServletHelper.getHttpServletResponse().getContentType();
    }

    public static void setContentType(String type) {
        CurrentServletHelper.getHttpServletResponse().setContentType(type);
    }

    public static ServletOutputStream getOutputStream() throws IOException {
        return CurrentServletHelper.getHttpServletResponse().getOutputStream();
    }

    public static PrintWriter getWriter() throws IOException {
        return CurrentServletHelper.getHttpServletResponse().getWriter();
    }

    public static void setContentLength(int len) {
        CurrentServletHelper.getHttpServletResponse().setContentLength(len);
    }

    public static void setContentLengthLong(long len) {
        CurrentServletHelper.getHttpServletResponse().setContentLengthLong(len);
    }

    public static int getBufferSize() {
        return CurrentServletHelper.getHttpServletResponse().getBufferSize();
    }

    public static void setBufferSize(int size) {
        CurrentServletHelper.getHttpServletResponse().setBufferSize(size);
    }

    public static void flushBuffer() throws IOException {
        CurrentServletHelper.getHttpServletResponse().flushBuffer();
    }

    public static void resetBuffer() {
        CurrentServletHelper.getHttpServletResponse().resetBuffer();
    }

    public static boolean isCommitted() {
        return CurrentServletHelper.getHttpServletResponse().isCommitted();
    }

    public static void reset() {
        CurrentServletHelper.getHttpServletResponse().reset();
    }

    public static Locale getLocale() {
        return CurrentServletHelper.getHttpServletResponse().getLocale();
    }

    public static void setLocale(Locale loc) {
        CurrentServletHelper.getHttpServletResponse().setLocale(loc);
    }
}
