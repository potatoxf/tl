package pxf.tl.convert.impl;


import pxf.tl.api.Charsets;
import pxf.tl.convert.AbstractConverter;

import java.nio.charset.Charset;

/**
 * 编码对象转换器
 *
 * @author potatoxf
 */
public class CharsetConverter extends AbstractConverter<Charset> {
    private static final long serialVersionUID = 1L;

    @Override
    protected Charset convertInternal(Object value) {
        return Charsets.parseCharset(convertToStr(value));
    }
}
