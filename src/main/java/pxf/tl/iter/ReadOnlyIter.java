package pxf.tl.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author potatoxf
 */
public class ReadOnlyIter<T> implements Iterator<T> {
    private final Iterator<T> iterator;

    public ReadOnlyIter(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public T next() {
        return iterator.next();
    }
}
