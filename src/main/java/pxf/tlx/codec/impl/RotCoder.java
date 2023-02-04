package pxf.tlx.codec.impl;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import pxf.tlx.codec.StringCharArrayCoder;

/**
 * rot13字符串编码
 *
 * <p>该类是线程安全的，因为它的属性是不变的，并且其余的都是瞬时对象
 * <p>
 * RotN（rotate by N places），回转N位密码，是一种简易的替换式密码，也是过去在古罗马开发的凯撒加密的一种变体。<br>
 * 代码来自：https://github.com/orclight/jencrypt
 *
 * @author potatoxf
 */
public class RotCoder implements StringCharArrayCoder {
    private static final char aCHAR = 'a';
    private static final char zCHAR = 'z';
    private static final char ACHAR = 'A';
    private static final char ZCHAR = 'Z';
    private static final char CHAR0 = '0';
    private static final char CHAR9 = '9';
    private final int offset;
    private final boolean isIncludeNumber;

    public RotCoder(int offset, boolean isIncludeNumber) {
        this.offset = offset;
        this.isIncludeNumber = isIncludeNumber;
    }

    /**
     * 解码字符
     *
     * @param c              字符
     * @param offset         位移
     * @param isDecodeNumber 是否解码数字
     * @return 解码后的字符串
     */
    private static char encodeChar(char c, int offset, boolean isDecodeNumber) {
        if (isDecodeNumber) {
            if (c >= CHAR0 && c <= CHAR9) {
                c -= CHAR0;
                c = (char) ((c + offset) % 10);
                c += CHAR0;
            }
        }

        // A == 65, Z == 90
        if (c >= ACHAR && c <= ZCHAR) {
            c -= ACHAR;
            c = (char) ((c + offset) % 26);
            c += ACHAR;
        }
        // a == 97, z == 122.
        else if (c >= aCHAR && c <= zCHAR) {
            c -= aCHAR;
            c = (char) ((c + offset) % 26);
            c += aCHAR;
        }
        return c;
    }

    /**
     * 编码字符
     *
     * @param c              字符
     * @param offset         位移
     * @param isDecodeNumber 是否编码数字
     * @return 编码后的字符串
     */
    private static char decodeChar(char c, int offset, boolean isDecodeNumber) {
        int temp = c;
        // if converting numbers is enabled
        if (isDecodeNumber) {
            if (temp >= CHAR0 && temp <= CHAR9) {
                temp -= CHAR0;
                temp = temp - offset;
                while (temp < 0) {
                    temp += 10;
                }
                temp += CHAR0;
            }
        }

        // A == 65, Z == 90
        if (temp >= ACHAR && temp <= ZCHAR) {
            temp -= ACHAR;

            temp = temp - offset;
            while (temp < 0) {
                temp = 26 + temp;
            }
            temp += ACHAR;
        } else if (temp >= aCHAR && temp <= zCHAR) {
            temp -= aCHAR;

            temp = temp - offset;
            if (temp < 0) temp = 26 + temp;

            temp += aCHAR;
        }
        return (char) temp;
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
        final char[] chars = new char[source.length];
        for (int i = 0; i < source.length; i++) {
            chars[i] = decodeChar(source[i], offset, isIncludeNumber);
        }
        return chars;
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
        final char[] chars = new char[source.length];
        for (int i = 0; i < source.length; i++) {
            chars[i] = encodeChar(source[i], offset, isIncludeNumber);
        }
        return chars;
    }

}
