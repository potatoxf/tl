package pxf.tl.collection;

import pxf.tl.api.Sized;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * @author potatoxf
 */
public interface ExtendMap<K, V> extends Map<K, V>, Iterable<Map.Entry<K, V>>, Sized, Serializable {
    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    @Override
    default boolean isEmpty() {
        return Sized.super.isEmpty();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    default Iterator<Entry<K, V>> iterator() {
        return entrySet().iterator();
    }
}
