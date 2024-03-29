package pxf.tl.cache.file;


import pxf.tl.cache.Cache;
import pxf.tl.cache.impl.LRUCache;

import java.io.File;

/**
 * 使用LRU缓存文件，以解决频繁读取文件引起的性能问题
 *
 * @author potatoxf
 */
public class LRUFileCache extends AbstractFileCache {
    private static final long serialVersionUID = 1L;

    /**
     * 构造<br>
     * 最大文件大小为缓存容量的一半<br>
     * 默认无超时
     *
     * @param capacity 缓存容量
     */
    public LRUFileCache(int capacity) {
        this(capacity, capacity / 2, 0);
    }

    /**
     * 构造<br>
     * 默认无超时
     *
     * @param capacity    缓存容量
     * @param maxFileSize 最大文件大小
     */
    public LRUFileCache(int capacity, int maxFileSize) {
        this(capacity, maxFileSize, 0);
    }

    /**
     * 构造
     *
     * @param capacity    缓存容量
     * @param maxFileSize 文件最大大小
     * @param timeout     默认超时时间，0表示无默认超时
     */
    public LRUFileCache(int capacity, int maxFileSize, long timeout) {
        super(capacity, maxFileSize, timeout);
    }

    @Override
    protected Cache<File, byte[]> initCache() {
        return new LRUCache<>(LRUFileCache.this.capacity, super.timeout) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isFull() {
                return LRUFileCache.this.usedSize > this.capacity;
            }

            @Override
            protected void onRemove(File key, byte[] cachedObject) {
                usedSize -= cachedObject.length;
            }
        };
    }
}
