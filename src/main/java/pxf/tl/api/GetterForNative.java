package pxf.tl.api;

/**
 * 原生类型获取器
 *
 * @author potatoxf
 */
public interface GetterForNative extends GetterForNumber {

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    Boolean getBooleanValue();

    /**
     * 获取元素，如果不存在或是不符合要求返回null
     *
     * @return 返回获取到的值，如果不存在或是不符合要求返回null
     */
    Character getCharacterValue();

    /**
     * 获取元素，如果不存在或是不符合要求返回默认值
     *
     * @param defaultValue 默认值
     * @return 返回获取到的值，如果不存在或是不符合要求返回默认值
     */
    default boolean gainBooleanValue(boolean defaultValue) {
        Boolean value = getBooleanValue();
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
    default char gainCharValue(char defaultValue) {
        Character value = getCharacterValue();
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
