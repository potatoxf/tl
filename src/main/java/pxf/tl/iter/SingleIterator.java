package pxf.tl.iter;

import java.util.NoSuchElementException;

/**
 * a object Iterator
 *
 * @author potatoxf
 */
public class SingleIterator<T> implements Iter<T> {

    private T single;

    public SingleIterator(T single) {
        this.single = single;
    }

    /**
     * Returns {@code true} if the iteration has more elements. (In other words, returns {@code true}
     * if {@link #next} would return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        return single != null;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public T next() {
        T next = single;
        single = null;
        return next;
    }
}
