package pxf.tl.collection.map;


import pxf.tl.function.SupplierThrow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author potatoxf
 */
public class ListMap<K, V> extends MapWrapper<K, List<V>> {

    private final SupplierThrow<List<V>, RuntimeException> listSupplierThrow;

    public ListMap() {
        this(ArrayList::new);
    }

    public ListMap(SupplierThrow<List<V>, RuntimeException> listSupplierThrow) {
        this(new HashMap<K, List<V>>(), listSupplierThrow);
    }

    public ListMap(
            Map<K, List<V>> proxyMap, SupplierThrow<List<V>, RuntimeException> listSupplierThrow) {
        super(proxyMap);
        this.listSupplierThrow = listSupplierThrow;
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
    public List<V> put(K key, List<V> value) {
        return super.put(key, value);
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
    public List<V> putOne(K key, V value) {
        List<V> vs = get(key);
        if (vs == null) {
            vs = listSupplierThrow.get();
            return super.put(key, vs);
        }
        vs.add(value);
        return vs;
    }
}
