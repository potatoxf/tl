package pxf.tl.exception;


import pxf.tl.util.ToolException;
import pxf.tl.util.ToolString;

import java.io.Serial;

/**
 * 克隆异常
 *
 * @author potatoxf
 */
public class CloneRuntimeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public CloneRuntimeException(Throwable e) {
        super(ToolException.getMessage(e), e);
    }

    public CloneRuntimeException(String message) {
        super(message);
    }

    public CloneRuntimeException(String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params));
    }

    public CloneRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public CloneRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params), throwable);
    }
}
