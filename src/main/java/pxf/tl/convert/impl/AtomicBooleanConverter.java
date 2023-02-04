package pxf.tl.convert.impl;


import pxf.tl.convert.AbstractConverter;
import pxf.tl.util.ToolBoolean;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link AtomicBoolean}转换器
 *
 * @author potatoxf
 */
public class AtomicBooleanConverter extends AbstractConverter<AtomicBoolean> {
    private static final long serialVersionUID = 1L;

    @Override
    protected AtomicBoolean convertInternal(Object value) {
        if (value instanceof Boolean) {
            return new AtomicBoolean((Boolean) value);
        }
        final String valueStr = convertToStr(value);
        return new AtomicBoolean(ToolBoolean.toBoolean(valueStr));
    }
}
