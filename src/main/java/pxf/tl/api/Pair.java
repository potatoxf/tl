package pxf.tl.api;

import pxf.tl.exception.UnsupportedException;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * @author potatoxf
 */
public class Pair<K, V> implements Map.Entry<K, V>, Serializable {
    protected volatile K key;
    protected volatile V value;

    public Pair(K key) {
        this(key, null);
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedException("setValue");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair<?, ?> pair)) return false;
        return Objects.equals(key, pair.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return key + " = " + value;
    }
}
