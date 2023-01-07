package pxf.tl.iter;

import java.util.Iterator;

/**
 * 提供合成接口，共同提供{@link Iterable}和{@link Iterator}功能
 *
 * @param <T> 节点类型
 * @author potatoxf
 */
public interface Iter<T> extends Iterable<T>, Iterator<T> {

    /**
     * 重置，重置后可重新遍历
     */
    default void reset() {
        throw new UnsupportedOperationException("reset");
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    default Iterator<T> iterator() {
        return this;
    }
}
