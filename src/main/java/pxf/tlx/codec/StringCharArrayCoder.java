package pxf.tlx.codec;

import pxf.tl.api.Charsets;

/**
 * @author potatoxf
 */
public interface StringCharArrayCoder extends ConvertStringCoder<char[]> {
    @Override
    default boolean isSupportSame() {
        return true;
    }

    @Override
    default char[] convert(String source, Charsets charsets) {
        return source.toCharArray();
    }

    @Override
    default String convert(char[] source, Charsets charsets) {
        return new String(source);
    }
}
