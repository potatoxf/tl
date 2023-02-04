package pxf.tl.iter;

import java.util.Iterator;

/**
 * 分页可迭代器
 *
 * @author potatoxf
 */
public final class PaginationIterable<T> implements Iterable<T> {

    private final PaginationIterator<T> template;

    /**
     * @param paginationDataLoader
     */
    public PaginationIterable(PaginationDataLoader<T> paginationDataLoader) {
        this(100, paginationDataLoader);
    }

    /**
     * @param pageSize
     * @param paginationDataLoader
     */
    public PaginationIterable(int pageSize, PaginationDataLoader<T> paginationDataLoader) {
        this(false, pageSize, paginationDataLoader);
    }

    /**
     * @param isCache
     * @param pageSize
     * @param paginationDataLoader
     */
    public PaginationIterable(
            boolean isCache, int pageSize, PaginationDataLoader<T> paginationDataLoader) {
        this.template = new PaginationIterator<T>(isCache, pageSize, paginationDataLoader);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<T> iterator() {
        return new PaginationIterator<>(template);
    }
}
