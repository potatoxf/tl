package pxf.tl.api;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author potatoxf
 */
public interface GetterForValue extends GetterForNative {

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    BigInteger getBigIntegerValue();

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    BigDecimal getBigDecimalValue();

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    String getStringValue();

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    Object getObjectValue();

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    default BigInteger gainBigIntegerValue(BigInteger defaultValue) {
        BigInteger value = getBigIntegerValue();
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    default BigDecimal gainBigDecimalValue(BigDecimal defaultValue) {
        BigDecimal value = getBigDecimalValue();
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    default String gainStringValue(String defaultValue) {
        String value = getStringValue();
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    default Object gainObjectValue(Object defaultValue) {
        Object value = getObjectValue();
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
