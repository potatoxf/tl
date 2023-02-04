package pxf.tl.setting;


import pxf.tl.util.ToolString;

/**
 * 设置异常
 *
 * @author potatoxf
 */
public class SettingRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 7941096116780378387L;

    public SettingRuntimeException(Throwable e) {
        super(e);
    }

    public SettingRuntimeException(String message) {
        super(message);
    }

    public SettingRuntimeException(String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params));
    }

    public SettingRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public SettingRuntimeException(
            String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public SettingRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
        super(ToolString.format(messageTemplate, params), throwable);
    }
}
