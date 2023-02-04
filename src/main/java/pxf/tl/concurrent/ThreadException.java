package pxf.tl.concurrent;


import pxf.tl.util.ToolException;
import pxf.tl.util.ToolString;

/**
 * 工具类异常
 *
 * @author potatoxf
 */
public class ThreadException extends RuntimeException {
    private static final long serialVersionUID = 5253124428623713216L;

    public ThreadException(Throwable e) {
        super(ToolException.getMessage(e), e);
    }

    public ThreadException(String message) {
        super(message);
    }

    public ThreadException(String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params));
    }

    public ThreadException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ThreadException(
            String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public ThreadException(Throwable throwable, String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params), throwable);
    }
}
