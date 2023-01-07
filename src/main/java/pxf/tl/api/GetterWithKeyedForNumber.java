package pxf.tl.api;

/**
 * 数字类型获取器
 *
 * @author potatoxf
 */
public interface GetterWithKeyedForNumber<K> extends GetterForNumber {

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    Number getNumberValue(K key);

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    default Number getNumberValue() {
        return getNumberValue(null);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default Byte getByteValue(K key) {
        Number numberValue = getNumberValue(key);
        if (numberValue == null) {
            return null;
        }
        return numberValue.byteValue();
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default Short getShortValue(K key) {
        Number numberValue = getNumberValue(key);
        if (numberValue == null) {
            return null;
        }
        return numberValue.shortValue();
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default Integer getIntegerValue(K key) {
        Number numberValue = getNumberValue(key);
        if (numberValue == null) {
            return null;
        }
        return numberValue.intValue();
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default Long getLongValue(K key) {
        Number numberValue = getNumberValue(key);
        if (numberValue == null) {
            return null;
        }
        return numberValue.longValue();
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default Float getFloatValue(K key) {
        Number numberValue = getNumberValue(key);
        if (numberValue == null) {
            return null;
        }
        return numberValue.floatValue();
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default Double getDoubleValue(K key) {
        Number numberValue = getNumberValue(key);
        if (numberValue == null) {
            return null;
        }
        return numberValue.doubleValue();
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    default byte gainByteValue(K key, byte defaultValue) {
        Byte value = getByteValue(key);
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
    default short gainShortValue(K key, short defaultValue) {
        Short value = getShortValue(key);
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
    default int gainIntValue(K key, int defaultValue) {
        Integer value = getIntegerValue(key);
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
    default long gainLongValue(K key, long defaultValue) {
        Long value = getLongValue(key);
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
    default float gainFloatValue(K key, float defaultValue) {
        Float value = getFloatValue(key);
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
    default double gainDoubleValue(K key, double defaultValue) {
        Double value = getDoubleValue(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
