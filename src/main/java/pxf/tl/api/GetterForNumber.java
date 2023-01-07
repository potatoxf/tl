package pxf.tl.api;

/**
 * @author potatoxf
 */
public interface GetterForNumber {

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    Number getNumberValue();

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default Byte getByteValue() {
        Number numberValue = getNumberValue();
        if (numberValue == null) {
            return null;
        }
        return numberValue.byteValue();
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default Short getShortValue() {
        Number numberValue = getNumberValue();
        if (numberValue == null) {
            return null;
        }
        return numberValue.shortValue();
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default Integer getIntegerValue() {
        Number numberValue = getNumberValue();
        if (numberValue == null) {
            return null;
        }
        return numberValue.intValue();
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default Long getLongValue() {
        Number numberValue = getNumberValue();
        if (numberValue == null) {
            return null;
        }
        return numberValue.longValue();
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default Float getFloatValue() {
        Number numberValue = getNumberValue();
        if (numberValue == null) {
            return null;
        }
        return numberValue.floatValue();
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    default Double getDoubleValue() {
        Number numberValue = getNumberValue();
        if (numberValue == null) {
            return null;
        }
        return numberValue.doubleValue();
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    default byte gainByteValue(byte defaultValue) {
        Byte value = getByteValue();
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
    default short gainShortValue(short defaultValue) {
        Short value = getShortValue();
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
    default int gainIntValue(int defaultValue) {
        Integer value = getIntegerValue();
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
    default long gainLongValue(long defaultValue) {
        Long value = getLongValue();
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
    default float gainFloatValue(float defaultValue) {
        Float value = getFloatValue();
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
    default double gainDoubleValue(double defaultValue) {
        Double value = getDoubleValue();
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
