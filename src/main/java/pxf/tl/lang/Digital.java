package pxf.tl.lang;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pxf.tl.util.ToolLog;

import java.math.BigDecimal;

/**
 * @author potatoxf
 */
public class Digital extends AbstractDigital {
    private static final Logger LOGGER = LoggerFactory.getLogger(Digital.class);
    private final Object number;
    private final Number defaultNumber;
    private Number cacheNumber;

    public Digital(Object number, Number defaultNumber) {
        this.number = number;
        this.defaultNumber = defaultNumber;
    }

    /**
     * 获取数字对象
     *
     * @param defaultValue 默认之
     * @return {@code Number}
     */
    @Override
    protected Number parseNumber(Number defaultValue) {
        if (cacheNumber != null) {
            return cacheNumber;
        }
        if (number instanceof Number) {
            cacheNumber = (Number) number;
            return cacheNumber;
        }
        if (number instanceof String) {
            try {
                cacheNumber = new BigDecimal(number.toString());
                return cacheNumber;
            } catch (NumberFormatException e) {
                ToolLog.error(LOGGER, e, () -> "");
            }
        }
        if (defaultValue != null) {
            return defaultValue;
        }
        return defaultNumber;
    }

    /**
     * 获取数字对象
     *
     * @return {@code Number}
     */
    @Override
    protected Number configDefaultNumber() {
        return 0;
    }
}
