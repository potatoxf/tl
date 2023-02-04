package pxf.tl.collection;


import pxf.tl.api.Sized;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author potatoxf
 */
public interface ExtendCollection<E> extends Collection<E>, Sized, Serializable {
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
