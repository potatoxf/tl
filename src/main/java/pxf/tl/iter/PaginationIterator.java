package pxf.tl.iter;

import pxf.tl.help.Whether;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 分页迭代器
 *
 * @author potatoxf
 */
public final class PaginationIterator<T> implements Iterator<T> {

    /**
     *
     */
    private final int pageSize;
    /**
     *
     */
    private final PaginationDataLoader<T> paginationDataLoader;
    /**
     *
     */
    private List<T> cache;
    /**
     *
     */
    private int currentPage;
    /**
     *
     */
    private Iterator<T> currentIterator;
    /**
     *
     */
    private int currentSize;

    /**
     * @param paginationDataLoader
     */
    public PaginationIterator(PaginationDataLoader<T> paginationDataLoader) {
        this(100, paginationDataLoader);
    }

    /**
     * @param pageSize
     * @param paginationDataLoader
     */
    public PaginationIterator(int pageSize, PaginationDataLoader<T> paginationDataLoader) {
        this(false, pageSize, paginationDataLoader);
    }

    /**
     * @param isCache
     * @param pageSize
     * @param paginationDataLoader
     */
    public PaginationIterator(
            boolean isCache, int pageSize, PaginationDataLoader<T> paginationDataLoader) {
        this.pageSize = pageSize;
        this.paginationDataLoader = paginationDataLoader;
        if (isCache) {
            cache = new LinkedList<>();
        }
        this.currentPage = 0;
        this.currentIterator = null;
        this.currentSize = -1;
    }

    /**
     * @param paginationIterator
     */
    public PaginationIterator(PaginationIterator<T> paginationIterator) {
        this.pageSize = paginationIterator.pageSize;
        this.paginationDataLoader = paginationIterator.paginationDataLoader;
        if (paginationIterator.cache != null) {
            this.cache = new LinkedList<>();
        }
        this.currentPage = 0;
        this.currentIterator = null;
        this.currentSize = -1;
    }

    /**
     * Returns {@code true} if the iteration has more elements. (In other words, returns {@code true}
     * if {@link #next} would return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        if (currentIterator == null || !currentIterator.hasNext()) {
            try {
                List<T> currentList = paginationDataLoader.load(pageSize, ++currentPage);
                if (currentList != null && !Whether.empty(currentList)) {
                    if (currentSize != -1 && currentSize != pageSize) {
                        throw new RuntimeException();
                    }
                    if (cache != null) {
                        cache.addAll(currentList);
                    }
                    currentSize = currentList.size();
                    currentIterator = currentList.iterator();
                    return true;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            currentIterator = null;
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public T next() {
        return currentIterator.next();
    }

    /**
     * @return
     */
    public List<T> getCache() {
        if (cache != null && currentSize == -1) {
            loadAll();
        }
        return cache;
    }

    /**
     *
     */
    public void loadAll() {
        while (hasNext()) next();
    }

    /**
     * @return
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * @return
     */
    public int size() {
        if (cache != null) {
            return cache.size();
        }
        return (currentPage - 1) * pageSize + currentSize;
    }
}
