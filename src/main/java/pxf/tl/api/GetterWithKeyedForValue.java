package pxf.tl.api;

import pxf.tl.help.Safe;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author potatoxf
 */
public interface GetterWithKeyedForValue<K> extends GetterWithKeyedForNative<K>, GetterForValue {

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    Object getObjectValue(K key);

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    default Number getNumberValue(K key) {
        Object objectValue = getObjectValue(key);
        if (objectValue instanceof Number) {
            return (Number) objectValue;
        } else {
            return Safe.toBigDecimal(String.valueOf(objectValue), null);
        }
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    default Boolean getBooleanValue(K key) {
        Object objectValue = getObjectValue(key);
        if (objectValue instanceof Boolean) {
            return (Boolean) objectValue;
        } else {
            return Safe.toBoolean(String.valueOf(objectValue), null);
        }
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    default Character getCharacterValue(K key) {
        Object objectValue = getObjectValue(key);
        if (objectValue instanceof Character) {
            return (Character) objectValue;
        } else {
            return Safe.toCharacter(String.valueOf(objectValue), null);
        }
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default String getStringValue(K key) {
        return Safe.value(getObjectValue(key), (String) null);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default BigInteger getBigIntegerValue(K key) {
        return Safe.toBigInteger(getNumberValue(key), null);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default BigDecimal getBigDecimalValue(K key) {
        return Safe.toBigDecimal(getNumberValue(key), null);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    default BigInteger getBigIntegerValue() {
        return getBigIntegerValue(null);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    default BigDecimal getBigDecimalValue() {
        return getBigDecimalValue(null);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    default String getStringValue() {
        return getStringValue(null);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    default Object getObjectValue() {
        return getObjectValue(null);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    default BigInteger gainBigIntegerValue(K key, BigInteger defaultValue) {
        BigInteger value = getBigIntegerValue(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    default BigDecimal gainBigDecimalValue(K key, BigDecimal defaultValue) {
        BigDecimal value = getBigDecimalValue(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    default String gainStringValue(K key, String defaultValue) {
        String value = getStringValue(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    default Object gainObjectValue(K key, Object defaultValue) {
        Object value = getObjectValue(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
