package pxf.tl.collection;


import pxf.tl.util.ToolCollection;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author potatoxf
 */
public class BidirectionalMap<K, V> implements ExtendMap<K, V> {
    private final Map<K, V> main = new LinkedHashMap<K, V>();
    private final Map<V, K> reserve = new LinkedHashMap<V, K>();

    @Override
    public int size() {
        return main.size();
    }

    @Override
    public boolean containsKey(Object key) {
        return main.containsKey(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(Object value) {
        return reserve.containsKey((V) value);
    }

    @Override
    public V get(Object key) {
        return main.get(key);
    }

    @Override
    public V put(K key, V value) {
        reserve.put(value, key);
        return main.put(key, value);
    }

    @Override
    public V remove(Object key) {
        reserve.remove(get(key));
        return main.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        ToolCollection.reserveMap(m, reserve);
        main.putAll(m);
    }

    @Override
    public void clear() {
        reserve.clear();
        main.clear();
    }

    @Override
    public Set<K> keySet() {
        return main.keySet();
    }

    @Override
    public Collection<V> values() {
        return reserve.keySet();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return main.entrySet();
    }

    public K getKey(V value) {
        return reserve.get(value);
    }

    public K removeKey(V value) {
        return reserve.remove(value);
    }
}
