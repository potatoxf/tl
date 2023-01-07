package pxf.tl.convert.impl;


import pxf.tl.convert.AbstractConverter;
import pxf.tl.convert.Convert;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * {@link AtomicIntegerArray}转换器
 *
 * @author potatoxf
 */
public class AtomicIntegerArrayConverter extends AbstractConverter<AtomicIntegerArray> {
    private static final long serialVersionUID = 1L;

    @Override
    protected AtomicIntegerArray convertInternal(Object value) {
        return new AtomicIntegerArray(Convert.convert(int[].class, value));
    }
}
