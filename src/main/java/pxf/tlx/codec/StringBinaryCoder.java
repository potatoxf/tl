package pxf.tlx.codec;

import pxf.tl.api.Charsets;
import pxf.tl.help.New;

/**
 * @author potatoxf
 */
public interface StringBinaryCoder extends ConvertStringCoder<byte[]> {
    @Override
    default boolean isSupportSame() {
        return true;
    }

    @Override
    default byte[] convert(String source, Charsets charsets) {
        return New.bytes(source, charsets);
    }

    @Override
    default String convert(byte[] source, Charsets charsets) {
        return New.string(source, charsets);
    }
}
