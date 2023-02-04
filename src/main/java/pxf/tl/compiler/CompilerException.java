package pxf.tl.compiler;


import pxf.tl.util.ToolException;
import pxf.tl.util.ToolString;

/**
 * 编译异常
 *
 * @author potatoxf
 */
public class CompilerException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CompilerException(Throwable e) {
        super(ToolException.getMessage(e), e);
    }

    public CompilerException(String message) {
        super(message);
    }

    public CompilerException(String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params));
    }

    public CompilerException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public CompilerException(Throwable throwable, String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params), throwable);
    }
}
