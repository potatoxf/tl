package pxf.tl.iter;


import pxf.tl.function.FunctionThrow;

import java.util.Iterator;

/**
 * 映射元素迭代器
 *
 * @author potatoxf
 */
public class MappingIterator<O, T> implements Iterator<T> {

    /**
     *
     */
    private final Iterator<O> iterator;
    /**
     *
     */
    private final FunctionThrow<O, T, RuntimeException> factoryThrow;

    /**
     * @param iterator
     * @param factoryThrow
     */
    public MappingIterator(Iterator<O> iterator, FunctionThrow<O, T, RuntimeException> factoryThrow) {
        if (iterator == null) {
            throw new NullPointerException();
        }
        if (factoryThrow == null) {
            throw new NullPointerException();
        }
        this.iterator = iterator;
        this.factoryThrow = factoryThrow;
    }

    /**
     * @return
     */
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * @return
     */
    @Override
    public T next() {
        return factoryThrow.apply(iterator.next());
    }

    /**
     *
     */
    @Override
    public void remove() {
        iterator.remove();
    }
}
