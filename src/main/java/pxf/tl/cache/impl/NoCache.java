package pxf.tl.cache.impl;


import pxf.tl.cache.Cache;

import java.io.Serial;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * 无缓存实现，用于快速关闭缓存
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author potatoxf
 */
public class NoCache<K, V> implements Cache<K, V> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public long timeout() {
        return 0;
    }

    @Override
    public void put(K key, V object) {
    }

    @Override
    public void put(K key, V object, long timeout) {
    }

    @Override
    public boolean containsKey(K key) {
        return false;
    }

    @Override
    public V get(K key, boolean isUpdateLastAccess) {
        return null;
    }

    @Override
    public V get(K key, boolean isUpdateLastAccess, Supplier<V> supplier) {
        return (null == supplier) ? null : supplier.get();
    }

    @Override
    public Iterator<V> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public Iterator<CacheObj<K, V>> cacheObjIterator() {
        return Collections.emptyIterator();
    }

    @Override
    public int prune() {
        return 0;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public void remove(K key) {
    }

    @Override
    public void clear() {
    }

    @Override
    public int size() {
        return 0;
    }
}
