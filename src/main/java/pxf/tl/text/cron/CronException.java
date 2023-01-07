package pxf.tl.text.cron;


import pxf.tl.util.ToolString;

/**
 * 定时任务异常
 *
 * @author potatoxf
 */
public class CronException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CronException(Throwable e) {
        super(e.getMessage(), e);
    }

    public CronException(String message) {
        super(message);
    }

    public CronException(String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params));
    }

    public CronException(
            String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public CronException(Throwable throwable, String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params), throwable);
    }
}
