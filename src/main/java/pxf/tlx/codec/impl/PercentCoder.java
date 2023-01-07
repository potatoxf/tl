package pxf.tlx.codec.impl;


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import pxf.tl.api.Charsets;
import pxf.tl.api.PoolOfCharacter;
import pxf.tl.help.New;
import pxf.tl.math.Hex;
import pxf.tl.net.RFC3986;
import pxf.tlx.codec.StringCharArrayCoder;

import javax.annotation.Nullable;
import java.util.BitSet;

/**
 * 百分号编码(Percent-encoding), 也称作URL编码(URL encoding)。<br>
 * 百分号编码可用于URI的编码，也可以用于"application/x-www-form-urlencoded"的MIME准备数据。
 *
 * <p>百分号编码会对 URI 中不允许出现的字符或者其他特殊情况的允许的字符进行编码，对于被编码的字符，最终会转为以百分号"%“开头，后面跟着两位16进制数值的形式。
 * 举个例子，空格符（SP）是不允许的字符，在 ASCII 码对应的二进制值是"00100000”，最终转为"%20"。
 *
 * <p>对于不同场景应遵循不同规范：
 *
 * <ul>
 *   <li>URI：遵循RFC 3986保留字规范
 *   <li>application/x-www-form-urlencoded，遵循W3C HTML Form content types规范，如空格须转+
 * </ul>
 *
 * @author potatoxf
 */
public class PercentCoder implements StringCharArrayCoder {
    /**
     * application/x-www-form-urlencoded，遵循W3C HTML Form content types规范，如空格须转+，+须被编码<br>
     * 规范见：https://url.spec.whatwg.org/#urlencoded-serializing
     * query中的value，默认除"-", "_", ".", "*"外都编码<br>
     * 这个类似于JDK提供的{@link java.net.URLEncoder}
     */
    public static final PercentCoder ALL = RFC3986.UNRESERVED.orNew("*", "~").setEncodeSpaceAsPlus(true);
    /**
     * 存放安全编码
     */
    private final BitSet safeCharacters = new BitSet(256);
    /**
     * 是否编码空格为+<br>
     * 如果为{@code true}，则将空格编码为"+"，此项只在"application/x-www-form-urlencoded"中使用<br>
     * 如果为{@code false}，则空格编码为"%20",此项一般用于URL的Query部分（RFC3986规范）
     */
    private boolean encodeSpaceAsPlus;
    /**
     *
     */
    private Charsets charsets;

    public static PercentCoder of(@Nullable String addChars) {
        return of(null, addChars, null);
    }

    public static PercentCoder of(@Nullable BitSet oldBitSet) {
        return of(oldBitSet, null, null);
    }

    public static PercentCoder ofInstance(@Nullable PercentCoder otherPercentCoder, @Nullable String addChars, @Nullable String delChars) {
        if (otherPercentCoder == null) {
            return of(null, addChars, delChars);
        } else {
            PercentCoder percentCoder = of(otherPercentCoder.safeCharacters, addChars, delChars);
            percentCoder.charsets = otherPercentCoder.charsets;
            percentCoder.encodeSpaceAsPlus = otherPercentCoder.encodeSpaceAsPlus;
            return percentCoder;
        }
    }

    public static PercentCoder of(@Nullable BitSet oldBitSet1, @Nullable BitSet oldBitSet2) {
        PercentCoder percentCoder = new PercentCoder();
        if (oldBitSet1 != null) {
            percentCoder.safeCharacters.or(oldBitSet1);
        }
        if (oldBitSet2 != null) {
            percentCoder.safeCharacters.or(oldBitSet2);
        }
        return percentCoder;
    }

    public static PercentCoder of(@Nullable BitSet oldBitSet, @Nullable String addChars, @Nullable String delChars) {
        PercentCoder percentCoder = new PercentCoder();
        if (oldBitSet != null) {
            percentCoder.safeCharacters.or(oldBitSet);
        }
        if (addChars != null) {
            for (int i = 0; i < addChars.length(); i++) {
                percentCoder.safeCharacters.set(addChars.charAt(i));
            }
        }
        if (delChars != null) {
            for (int i = 0; i < delChars.length(); i++) {
                percentCoder.safeCharacters.set(delChars.charAt(i));
            }
        }
        return percentCoder;
    }

    private PercentCoder() {
    }

    public PercentCoder setEncodeSpaceAsPlus(boolean encodeSpaceAsPlus) {
        this.encodeSpaceAsPlus = encodeSpaceAsPlus;
        return this;
    }

    public PercentCoder setCharsets(Charsets charsets) {
        this.charsets = charsets;
        return this;
    }

    /**
     * @param addChars
     * @return
     */
    public PercentCoder orNew(@Nullable String addChars) {
        return orNew(addChars, null);
    }

    /**
     * @param otherPercentCoder
     * @return
     */
    public PercentCoder orNew(@Nullable PercentCoder otherPercentCoder) {
        return orNew(otherPercentCoder, null, null);
    }

    /**
     * @param otherPercentCoder
     * @return
     */
    public PercentCoder orNew(@Nullable PercentCoder otherPercentCoder, @Nullable String addChars) {
        return orNew(otherPercentCoder, addChars, null);
    }

    /**
     * @param addChars
     * @param delChars
     * @return
     */
    public PercentCoder orNew(@Nullable String addChars, @Nullable String delChars) {
        return ofInstance(this, addChars, delChars);
    }

    /**
     * @param otherPercentCoder
     * @param addChars
     * @param delChars
     * @return
     */
    public PercentCoder orNew(@Nullable PercentCoder otherPercentCoder, @Nullable String addChars, @Nullable String delChars) {
        return orNew(otherPercentCoder == null ? null : otherPercentCoder.safeCharacters, addChars, delChars);
    }

    /**
     * @param oldBitSet
     * @param addChars
     * @param delChars
     * @return
     */
    public PercentCoder orNew(@Nullable BitSet oldBitSet, @Nullable String addChars, @Nullable String delChars) {
        return of(of(oldBitSet, addChars, delChars).safeCharacters, safeCharacters).setCharsets(charsets).setEncodeSpaceAsPlus(encodeSpaceAsPlus);
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
        return new char[0];
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
        final boolean encodeSpaceAsPlus;
        final Charsets charsets;
        synchronized (safeCharacters) {
            encodeSpaceAsPlus = this.encodeSpaceAsPlus;
            charsets = this.charsets;
        }
        final StringBuilder rewrittenPath = new StringBuilder(source.length);
        for (char c : source) {
            if (safeCharacters.get(c)) {
                rewrittenPath.append(c);
            } else if (encodeSpaceAsPlus && c == PoolOfCharacter.SPACE) {
                // 对于空格单独处理
                rewrittenPath.append('+');
            } else {
                byte[] bytes;
                if (charsets == null) {
                    bytes = New.bytes(c);
                } else {
                    bytes = New.bytes(String.valueOf(c), charsets);
                }
                for (byte b : bytes) {
                    rewrittenPath.append('%').append(Hex.encodeToString(b));
                }
            }
        }
        return rewrittenPath.toString().toCharArray();
    }
}
