package pxf.tl.convert.impl;


import pxf.tl.convert.AbstractConverter;
import pxf.tl.convert.Convert;

import java.util.concurrent.atomic.AtomicLongArray;

/**
 * {@link AtomicLongArray}转换器
 *
 * @author potatoxf
 */
public class AtomicLongArrayConverter extends AbstractConverter<AtomicLongArray> {
    private static final long serialVersionUID = 1L;

    @Override
    protected AtomicLongArray convertInternal(Object value) {
        return new AtomicLongArray(Convert.convert(long[].class, value));
    }
}
