package pxf.tlx.codec.impl;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import pxf.tl.api.PoolOfString;
import pxf.tl.api.InstanceSupplier;
import pxf.tl.math.Hex;
import pxf.tlx.codec.StringBinaryCoder;

import java.io.ByteArrayOutputStream;
import java.util.BitSet;

/**
 * URL二进制编码器
 *
 * <p>该类是线程安全的，因为它没有属性，都是瞬时对象
 *
 * @author potatoxf
 */
public class UrlCoder implements StringBinaryCoder {

    private static final InstanceSupplier<BitSet> UrlSafeCharBitSet = InstanceSupplier.of(() -> {
        BitSet safeChars = PoolOfString.getLetterNumberBitSet();
        // special chars
        safeChars.set('-');
        safeChars.set('_');
        safeChars.set('.');
        safeChars.set('*');
        // blank to be replaced with +
        safeChars.set(' ');
        return safeChars;
    });

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
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < source.length; i++) {
            int b = source[i];
            if (b == '+') {
                buffer.write(' ');
            } else if (b == '%') {
                try {
                    byte v = Hex.decodeToByte(new byte[]{source[++i], source[++i]});
                    buffer.write(v);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new DecoderException("Invalid URL encoding: ", e);
                }
            } else {
                buffer.write(b);
            }
        }
        return buffer.toByteArray();
    }

    /**
     * Encodes an "Object" and returns the encoded content as an Object. The Objects here may just be
     * {@code byte[]} or {@code String}s depending on the implementation used.
     *
     * @param source An object to encode
     * @return An "encoded" Object
     * @throws EncoderException An encoder exception is thrown if the encoder experiences a failure condition during the encoding
     *                          process.
     */
    @Override
    public byte[] encodeTargetThrow(byte[] source) throws EncoderException {
        BitSet WWW_FORM_URL_SAFE = UrlSafeCharBitSet.get();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (byte c : source) {
            int b = c;
            if (b < 0) {
                b = 256 + b;
            }
            if (WWW_FORM_URL_SAFE.get(b)) {
                if (b == ' ') {
                    b = '+';
                }
                buffer.write(b);
            } else {
                buffer.write('%');
                char[] chars = Hex.encodeToChars(b);
                for (char aChar : chars) {
                    buffer.write(aChar);
                }
            }
        }
        return buffer.toByteArray();
    }
}
