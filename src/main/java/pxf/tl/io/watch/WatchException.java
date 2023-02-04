package pxf.tl.io.watch;


import pxf.tl.util.ToolException;
import pxf.tl.util.ToolString;

/**
 * 监听异常
 *
 * @author potatoxf
 */
public class WatchException extends RuntimeException {
    private static final long serialVersionUID = 8068509879445395353L;

    public WatchException(Throwable e) {
        super(ToolException.getMessage(e), e);
    }

    public WatchException(String message) {
        super(message);
    }

    public WatchException(String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params));
    }

    public WatchException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public WatchException(Throwable throwable, String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params), throwable);
    }
}
