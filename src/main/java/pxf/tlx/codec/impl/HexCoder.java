package pxf.tlx.codec.impl;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import pxf.tl.math.Hex;
import pxf.tlx.codec.StringBinaryCoder;


/**
 * Hex二进制编码器
 *
 * <p>该类是线程安全的，因为它没有属性，都是瞬时对象
 *
 * @author potatoxf
 */
public class HexCoder implements StringBinaryCoder {

    public static final HexCoder INSTANCE = new HexCoder();

    private HexCoder() {
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
        return Hex.decodeToBytes(source);
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
        return Hex.encodeToBytes(source);
    }
}
