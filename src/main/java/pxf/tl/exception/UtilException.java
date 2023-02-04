package pxf.tl.exception;


import pxf.tl.util.ToolException;

/**
 * 工具类异常
 *
 * @author potatoxf
 */
public class UtilException extends RuntimeException {
    private static final long serialVersionUID = 8247610319171014183L;

    public UtilException(Throwable e) {
        super(ToolException.getMessage(e), e);
    }

    public UtilException(String message) {
        super(message);
    }

    public UtilException(String messageTemplate, Object... params) {
        super(String.format(messageTemplate, params));
    }

    public UtilException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public UtilException(
            String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public UtilException(Throwable throwable, String messageTemplate, Object... params) {
        super(String.format(messageTemplate, params), throwable);
    }
}
