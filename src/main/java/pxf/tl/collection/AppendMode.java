package pxf.tl.collection;

/**
 * 追加元素模式
 *
 * @author potatoxf
 */
public enum AppendMode {
    /**
     * 正常
     */
    NORMAL,
    /**
     * 超出忽略
     */
    BEYOND_IGNORED,
    /**
     * 超出覆盖，从前往后
     */
    BEYOND_OVERRIDE_BEFORE,
    /**
     * 超出覆盖，从后往前
     */
    BEYOND_OVERRIDE_AFTER,
    /**
     * 禁止
     */
    FORBID
}
