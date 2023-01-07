package pxf.tlx.codec.impl;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import pxf.tlx.codec.StringBinaryCoder;

import java.util.Base64;

/**
 * Base64二进制编码
 *
 * @author potatoxf
 */
public class Base64Coder implements StringBinaryCoder {

    public static final Base64Coder STRAND = new Base64Coder(0);
    public static final Base64Coder URL = new Base64Coder(1);
    public static final Base64Coder MIME = new Base64Coder(2);
    private final Base64.Encoder encoder;
    private final Base64.Decoder decoder;

    private Base64Coder(int base64Type) {
        switch (base64Type) {
            case 1 -> {
                this.encoder = Base64.getUrlEncoder();
                this.decoder = Base64.getUrlDecoder();
            }
            case 2 -> {
                this.encoder = Base64.getMimeEncoder();
                this.decoder = Base64.getMimeDecoder();
            }
            default -> {
                this.encoder = Base64.getEncoder();
                this.decoder = Base64.getDecoder();
            }
        }
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
        return this.decoder.decode(source);
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
        return this.encoder.encode(source);
    }
}
