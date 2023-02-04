package pxf.tl.convert.impl;


import pxf.tl.convert.AbstractConverter;

import java.util.UUID;

/**
 * UUID对象转换器转换器
 *
 * @author potatoxf
 */
public class UUIDConverter extends AbstractConverter<UUID> {
    private static final long serialVersionUID = 1L;

    @Override
    protected UUID convertInternal(Object value) {
        return UUID.fromString(convertToStr(value));
    }
}
