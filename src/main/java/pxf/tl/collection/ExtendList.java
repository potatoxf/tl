package pxf.tl.collection;


import pxf.tl.api.Sized;

import java.io.Serializable;
import java.util.List;

/**
 * @author potatoxf
 */
public interface ExtendList<E> extends List<E>, Sized, Serializable {
    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    @Override
    default boolean isEmpty() {
        return Sized.super.isEmpty();
    }
}
