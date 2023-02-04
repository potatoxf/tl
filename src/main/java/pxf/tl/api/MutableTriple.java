package pxf.tl.api;

import java.io.Serial;

/**
 * 可变{@link MutableTriple}实现，可以修改键和值
 *
 * @param <C> 类别类型
 * @param <K> 键类型
 * @param <V> 值类型
 * @author potatoxf
 */
public class MutableTriple<C, K, V> extends Triple<C, K, V> implements Mutable<Triple<C, K, V>> {
    @Serial
    private static final long serialVersionUID = 1L;

    public MutableTriple(C catalog, K key) {
        super(catalog, key);
    }

    public MutableTriple(C catalog, K key, V value) {
        super(catalog, key, value);
    }

    /**
     * 设置键
     *
     * @param catalog 新键
     * @return K
     */
    public C setCatalog(C catalog) {
        C oldCatalog = this.catalog;
        this.catalog = catalog;
        return oldCatalog;
    }

    /**
     * 设置键
     *
     * @param key 新键
     * @return K
     */
    public K setKey(K key) {
        K oldKey = this.key;
        this.key = key;
        return oldKey;
    }

    /**
     * 设置值
     *
     * @param value 新值
     * @return V
     */
    public V setValue(V value) {
        V oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    @Override
    public Triple<C, K, V> get() {
        return this;
    }

    @Override
    public void set(Triple<C, K, V> triple) {
        this.key = triple.getKey();
        this.value = triple.getValue();
    }
}
