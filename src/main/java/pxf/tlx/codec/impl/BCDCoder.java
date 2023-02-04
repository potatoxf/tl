package pxf.tlx.codec.impl;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import pxf.tl.api.Charsets;
import pxf.tlx.codec.StringBinaryCoder;

/**
 * BCD码（Binary-Coded Decimal）亦称二进码十进数或二-十进制代码<br>
 * BCD码这种编码形式利用了四个位元来储存一个十进制的数码，使二进制和十进制之间的转换得以快捷的进行<br>
 * see http://cuisuqiang.iteye.com/blog/1429956
 *
 * @author potatoxf
 */
public class BCDCoder implements StringBinaryCoder {
    public static final BCDCoder COMPRESS_INSTANCE = new BCDCoder(true);
    public static final BCDCoder NOCOMPRESS_INSTANCE = new BCDCoder(true);
    private final boolean compress;

    private BCDCoder(boolean compress) {
        this.compress = compress;
    }

    @Override
    public byte[] convert(String source, Charsets charsets) {
        return HexCoder.INSTANCE.decodeToTarget(source, charsets);
    }

    @Override
    public String convert(byte[] source, Charsets charsets) {
        return HexCoder.INSTANCE.encodeFromTarget(source, charsets);
    }

    /**
     * Decodes an "encoded" Object and returns a "decoded" Object. Note that the implementation of this interface will
     * try to cast the Object parameter to the specific type expected by a particular Decoder implementation. If a
     * {@link ClassCastException} occurs this decode method will throw a DecoderException.
     *
     * @param source the object to decode
     * @return a 'decoded" object
     * @throws DecoderException a decoder exception can be thrown for any number of reasons. Some good candidates are that the
     *                          parameter passed to this method is null, a param cannot be cast to the appropriate type for a
     *                          specific encoder.
     */
    @Override
    public byte[] decodeTargetThrow(byte[] source) throws DecoderException {
        byte[] result = new byte[source.length / (compress ? 2 : 3)];
        for (int i = 0; i < result.length; i++) {
            if (compress) {
                result[i] = (byte) ((source[2 * i] & 0xF) * 100 + ((source[2 * i + 1] >>> 4) & 0xF) * 10 + (source[2 * i + 1] & 0xF));
            } else {
                result[i] = (byte) ((source[3 * i] & 0xF) * 100 + (source[3 * i + 1] & 0xF) * 10 + (source[3 * i + 2] & 0xF));
            }
        }
        return result;
    }

    /**
     * Encodes an "Object" and returns the encoded content as an Object. The Objects here may just be
     * {@code byte[]} or {@code String}s depending on the implementation used.
     *
     * @param source An object to encode
     * @return An "encoded" Object
     * @throws EncoderException An encoder exception is thrown if the encoder experiences a failure condition during the encoding process.
     */
    @Override
    public byte[] encodeTargetThrow(byte[] source) throws EncoderException {
        byte[] bcd = new byte[source.length * (compress ? 2 : 3)];
        for (int i = 0; i < source.length; i++) {
            int v = source[i] & 0xFF;
            if (compress) {
                bcd[2 * i] = (byte) (v / 100 % 10);
                bcd[2 * i + 1] = (byte) (((v / 10 % 10) << 4) ^ (v % 10));
            } else {
                bcd[3 * i] = (byte) (v / 100 % 10);
                bcd[3 * i + 1] = (byte) (v / 10 % 10);
                bcd[3 * i + 2] = (byte) (v % 10);
            }
        }
        return bcd;
    }
}
