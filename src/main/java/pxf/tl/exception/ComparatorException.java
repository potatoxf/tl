package pxf.tl.exception;


import pxf.tl.util.ToolException;
import pxf.tl.util.ToolString;

/**
 * 比较异常
 *
 * @author potatoxf
 */
public class ComparatorException extends RuntimeException {

    public ComparatorException(Throwable e) {
        super(ToolException.getMessage(e), e);
    }

    public ComparatorException(String message) {
        super(message);
    }

    public ComparatorException(String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params));
    }

    public ComparatorException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ComparatorException(Throwable throwable, String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params), throwable);
    }
}
