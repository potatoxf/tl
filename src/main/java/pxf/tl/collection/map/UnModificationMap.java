package pxf.tl.collection.map;

import java.util.Map;

/**
 * 禁止修改Map适配器
 *
 * <p>只允许查找和增加，不允许修改和删除
 *
 * @author potatoxf
 */
public final class UnModificationMap<K, V> extends MapWrapper<K, V> {
    public UnModificationMap() {
    }

    public UnModificationMap(Map<K, V> proxyMap) {
        super(proxyMap);
    }

    /**
     * Associates the specified value with the specified key in this map (optional operation). If the
     * map previously contained a mapping for the key, the old value is replaced by the specified
     * value. (A map <tt>m</tt> is said to contain a mapping for a key <tt>k</tt> if and only if
     * {@link #containsKey(Object) m.containsKey(k)} would return <tt>true</tt>.)
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no
     * mapping for <tt>key</tt>. (A <tt>null</tt> return can also indicate that the map previously
     * associated <tt>null</tt> with <tt>key</tt>, if the implementation supports <tt>null</tt>
     * values.)
     * @throws UnsupportedOperationException if the <tt>put</tt> operation is not supported by this
     *                                       map
     * @throws ClassCastException            if the class of the specified key or value prevents it from being
     *                                       stored in this map
     * @throws NullPointerException          if the specified key or value is null and this map does not permit
     *                                       null keys or values
     * @throws IllegalArgumentException      if some property of the specified key or value prevents it
     *                                       from being stored in this map
     */
    @Override
    public V put(K key, V value) {
        if (containsKey(key)) {
            throw new UnsupportedOperationException(
                    "The entry is already included, modification of the value is not allowed");
        }
        return super.put(key, value);
    }

    /**
     * Removes the mapping for a key from this map if it is present (optional operation). More
     * formally, if this map contains a mapping from key <tt>k</tt> to value <tt>v</tt> such that
     * <code>(key==null ?  k==null : key.equals(k))</code>, that mapping is removed. (The map can
     * contain at most one such mapping.)
     *
     * <p>Returns the value to which this map previously associated the key, or <tt>null</tt> if the
     * map contained no mapping for the key.
     *
     * <p>If this map permits null values, then a return value of <tt>null</tt> does not
     * <i>necessarily</i> indicate that the map contained no mapping for the key; it's also possible
     * that the map explicitly mapped the key to <tt>null</tt>.
     *
     * <p>The map will not contain a mapping for the specified key apply the call returns.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no
     * mapping for <tt>key</tt>.
     * @throws UnsupportedOperationException This operation is not supported
     */
    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("Data deletion is not allowed");
    }

    /**
     * Removes all of the mappings from this map (optional operation). The map will be empty after
     * this call returns.
     *
     * @throws UnsupportedOperationException This operation is not supported
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("Erasing data is not allowed");
    }
}
