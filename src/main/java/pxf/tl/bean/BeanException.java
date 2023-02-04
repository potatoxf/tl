package pxf.tl.bean;


import pxf.tl.util.ToolException;
import pxf.tl.util.ToolString;

/**
 * Bean异常
 *
 * @author potatoxf
 */
public class BeanException extends RuntimeException {
    private static final long serialVersionUID = -8096998667745023423L;

    public BeanException(Throwable e) {
        super(ToolException.getMessage(e), e);
    }

    public BeanException(String message) {
        super(message);
    }

    public BeanException(String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params));
    }

    public BeanException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BeanException(Throwable throwable, String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params), throwable);
    }
}
