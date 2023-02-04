package pxf.tl.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 双重迭代器
 *
 * @author potatoxf
 */
public class BiIterator<T> implements Iterator<T> {

    /**
     * 内部迭代器
     */
    private final Iterator<? extends Iterable<T>> iteratorIterable;
    /**
     * 当前迭代器
     */
    private Iterator<T> current = null;

    /**
     * @param iteratorIterable
     */
    public BiIterator(Iterator<? extends Iterable<T>> iteratorIterable) {
        this.iteratorIterable = iteratorIterable;
    }

    /**
     * Returns {@code true} if the iteration has more elements. (In other words, returns {@code true}
     * if {@link #iteratorIterable} would return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        while (true) {
            if (current == null) {
                if (iteratorIterable.hasNext()) {
                    current = iteratorIterable.next().iterator();
                }
                if (current == null) {
                    break;
                }
            }
            if (current.hasNext()) {
                return true;
            } else {
                current = null;
            }
        }
        return false;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public T next() {
        return current.next();
    }
}
