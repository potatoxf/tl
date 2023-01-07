package pxf.tl.api;

/**
 * 有大小的
 *
 * @author potatoxf
 */
public interface Sized {

    /**
     * 是否有元素
     *
     * @return 如果空为 {@code true}，否则 {@code false}
     */
    default boolean isPresent() {
        return !isEmpty();
    }

    /**
     * 判读是否为空
     *
     * @return 如果为空则为 {@code true}，否则 {@code false}
     */
    default boolean isEmpty() {
        return size() <= 0;
    }


    /**
     * 判读是否不为空
     *
     * @return 如果不为空则为 {@code true}，否则 {@code false}
     */
    default boolean isNoEmpty() {
        return !isEmpty();
    }

    /**
     * 元素个数
     *
     * @return 元素个数
     */
    default int length() {
        return size();
    }

    /**
     * 元素个数
     *
     * @return 元素个数
     */
    int size();
}
