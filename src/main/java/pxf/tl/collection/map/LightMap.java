package pxf.tl.collection.map;


import pxf.tl.algs.RedBlackBiTree;
import pxf.tl.algs.RedBlackBiTreeNode;
import pxf.tl.lang.HashValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author potatoxf
 */
@SuppressWarnings("unchecked")
public class LightMap<K, V> extends AbstractMap<K, V> {

    private final int initializeCapacity;
    private final transient MapTree<K, V>[] table;
    private transient Set<MapNode<K, V>> entrySet;
    private transient int size;

    public LightMap(int initializeCapacity) {
        this.initializeCapacity = initializeCapacity;
        this.table = (MapTree<K, V>[]) new MapTree[initializeCapacity];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        int hash = hash(key);
        int i = (initializeCapacity - 1) & hash;
        MapTree<K, V> tree;
        if ((tree = table[i]) == null) {
            return null;
        }
        return tree.get(new HashValue(hash, key));
    }

    @Override
    public V put(K key, V value) {
        int hash = hash(key);
        int i = (initializeCapacity - 1) & hash;
        MapTree<K, V> tree;
        if ((tree = table[i]) == null) {
            tree = create();
        }
        return tree.put(new HashValue(hash, key), value);
    }

    @Override
    public V remove(Object key) {
        int hash = hash(key);
        int i = (initializeCapacity - 1) & hash;
        MapTree<K, V> tree;
        if ((tree = table[i]) == null) {
            return null;
        }
        return tree.delete(new HashValue(hash, key));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    private MapTree<K, V> create() {
        return new MapTree<K, V>();
    }

    private static class MapTree<K, V> extends RedBlackBiTree<HashValue, V, MapNode<HashValue, V>> {

        public MapTree() {
            super(MapNode::new);
        }
    }

    private static class MapNode<K, V> extends RedBlackBiTreeNode<HashValue, V, MapNode<K, V>>
            implements Entry<K, V> {

        public MapNode(HashValue key, V value) {
            super(key, value);
        }

        @Override
        public K getKey() {
            return (K) key().getValue();
        }

        @Override
        public V getValue() {
            return value();
        }

        @Override
        public V setValue(V value) {
            return updateValue(value);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }
}
