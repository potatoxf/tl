package pxf.tl.exception;


import pxf.tl.util.ToolException;
import pxf.tl.util.ToolString;

/**
 * 依赖异常
 *
 * @author potatoxf
 */
public class DependencyException extends RuntimeException {
    private static final long serialVersionUID = 8247610319171014183L;

    public DependencyException(Throwable e) {
        super(ToolException.getMessage(e), e);
    }

    public DependencyException(String message) {
        super(message);
    }

    public DependencyException(String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params));
    }

    public DependencyException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public DependencyException(
            String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public DependencyException(Throwable throwable, String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params), throwable);
    }
}
