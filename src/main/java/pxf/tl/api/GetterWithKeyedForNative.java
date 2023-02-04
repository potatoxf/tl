package pxf.tl.api;

/**
 * 数字类型获取器
 *
 * @author potatoxf
 */
public interface GetterWithKeyedForNative<K> extends GetterWithKeyedForNumber<K>, GetterForNative {

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    Boolean getBooleanValue(K key);

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @param key 键
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    Character getCharacterValue(K key);

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    default Boolean getBooleanValue() {
        return getBooleanValue(null);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    @Override
    default Character getCharacterValue() {
        return getCharacterValue(null);
    }

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    default boolean gainBooleanValue(K key, boolean defaultValue) {
        Boolean value = getBooleanValue(key);
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
    default char gainCharValue(K key, char defaultValue) {
        Character value = getCharacterValue(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
