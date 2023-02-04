package pxf.tlx.codec.impl;


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import pxf.tl.util.ToolString;
import pxf.tlx.codec.StringCharArrayCoder;

/**
 * 莫斯字符串编码
 *
 * <p>该类是线程安全的，因为它的属性是不变的，并且其余的都是瞬时对象
 *
 * @author potatoxf
 */
public class MorseStringCharArrayCoder implements StringCharArrayCoder {

    private final char space;

    public MorseStringCharArrayCoder() {
        this(' ');
    }

    public MorseStringCharArrayCoder(char space) {
        this.space = space;
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
    public char[] decodeTargetThrow(char[] source) throws DecoderException {
        StringBuilder word = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        int spaceCount = 0;
        for (int i = 0; i < source.length; i++) {
            char c = source[i];
            if (c == '.' || c == '-') {
                word.append(c);
            } else {
                sb.append(MorseCoder.parseMorseCode(word.toString()).toCharacterValue());
                word.setLength(0);
                if (c != space) {
                    sb.append(c);
                } else {
                    for (int j = i + 1; j < source.length; j++) {
                        c = source[i];
                        if (c != space && j - i != 1) {
                            ToolString.repeat(sb, space, j - i);
                        }
                    }
                }
            }
        }
        return sb.toString().toCharArray();
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
    public char[] encodeTargetThrow(char[] source) throws EncoderException {
        StringBuilder sb = new StringBuilder(source.length * 8);
        for (char c : source) {
            if (MorseCoder.contains(c)) {
                sb.append(MorseCoder.parseMorseCode(c).toMorseCode()).append(space);
            } else {
                sb.append(c);
            }
        }
        return sb.toString().toCharArray();
    }
}
