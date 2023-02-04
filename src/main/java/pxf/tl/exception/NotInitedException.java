package pxf.tl.exception;


import pxf.tl.util.ToolString;

/**
 * 未初始化异常
 *
 * @author potatoxf
 */
public class NotInitedException extends RuntimeException {
    private static final long serialVersionUID = 8247610319171014183L;

    public NotInitedException(Throwable e) {
        super(e);
    }

    public NotInitedException(String message) {
        super(message);
    }

    public NotInitedException(String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params));
    }

    public NotInitedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public NotInitedException(
            String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public NotInitedException(Throwable throwable, String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params), throwable);
    }
}
