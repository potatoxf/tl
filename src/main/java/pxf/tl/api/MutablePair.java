package pxf.tl.api;

import java.io.Serial;

/**
 * 可变{@link Pair}实现，可以修改键和值
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author potatoxf
 */
public class MutablePair<K, V> extends Pair<K, V> implements Mutable<Pair<K, V>> {
    @Serial
    private static final long serialVersionUID = 1L;

    public MutablePair(K key) {
        super(key);
    }

    /**
     * 构造
     *
     * @param key   键
     * @param value 值
     */
    public MutablePair(K key, V value) {
        super(key, value);
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
    public Pair<K, V> get() {
        return this;
    }

    @Override
    public void set(Pair<K, V> pair) {
        this.key = pair.getKey();
        this.value = pair.getValue();
    }
}
