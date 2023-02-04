package pxf.tl.text.escaper;


import pxf.tl.help.Whether;
import pxf.tl.math.Hex;

import javax.annotation.Nonnull;

/**
 * Unicode转义器
 *
 * @author potatoxf
 */
public final class UnicodeEscaper {

    private static final int ASCII_LIMIT = 255;
    /**
     * 前缀
     */
    private final String prefix;
    /**
     * 前缀长度
     */
    private final int prefixLength;
    /**
     * 后缀
     */
    private final String suffix;
    /**
     * 后缀长度
     */
    private final int suffixLength;

    public UnicodeEscaper() {
        this("\\u");
    }

    public UnicodeEscaper(String prefix) {
        this(prefix, "");
    }

    public UnicodeEscaper(String prefix, String suffix) {
        assert prefix != null && prefix.length() > 0 : "The prefix string must not be empty";
        assert suffix != null : "The suffix string must not be null";
        this.prefix = prefix;
        this.prefixLength = prefix.length();
        this.suffix = suffix;
        this.suffixLength = suffix.length();
    }

    /**
     * 从码点替换unicode
     *
     * @param input       输入字符串
     * @param deleteStart 删除字符串起始位置
     * @param deleteEnd   删除字符串结束位置
     * @param substrStart 子字符串起始位置
     * @param substrEnd   子字符串结束位置
     * @return 返回更新长度
     */
    private int replaceUnicodeFromCodepoint(
            StringBuilder input, int deleteStart, int deleteEnd, int substrStart, int substrEnd) {
        int codepoint;
        try {
            codepoint = Integer.parseInt(input.substring(substrStart, substrEnd), 16);
        } catch (NumberFormatException ignored) {
            return prefixLength;
        }
        char[] chars = Character.toChars(codepoint);
        input.delete(deleteStart, deleteEnd).insert(deleteStart, chars);
        return chars.length;
    }

    /**
     * 将unicode编码成转义代码
     *
     * @param input 输入字符串
     */
    @Nonnull
    public StringBuilder encode(@Nonnull StringBuilder input) {
        char[] cache4 = new char[4];
        char[] cache6 = new char[6];
        for (int i = 0; i < input.length(); i++) {
            int codepoint = input.codePointAt(i);
            if (codepoint <= ASCII_LIMIT) {
                continue;
            }
            input.deleteCharAt(i);
            // len for 8
            char[] chars = Hex.encodeToChars(codepoint);
            int l = 0;
            while (l < 4 && chars[l] == '0' && chars[l + 1] == '0') {
                l += 2;
            }
            input.insert(i, prefix);
            i += prefixLength;
            if (l == 2) {
                System.arraycopy(chars, l, cache6, 0, 6);
                input.insert(i, cache6);
                i += 6;
            } else if (l == 4) {
                System.arraycopy(chars, l, cache4, 0, 4);
                input.insert(i, cache4);
                i += 4;
            } else {
                input.insert(i, chars);
                i += 8;
            }
            input.insert(i, suffix);
            i += suffixLength;
            i--;
        }
        return input;
    }

    /**
     * 将unicode编码成转义代码
     *
     * @param input 输入字符串
     * @return 返回编码后的字符串
     */
    @Nonnull
    public String encode(@Nonnull String input) {
        return encode(new StringBuilder(input.length() * 8).append(input)).toString();
    }

    /**
     * 将带有unicode转义代码解码
     *
     * @param input 输入字符串
     */
    @Nonnull
    public StringBuilder decode(@Nonnull StringBuilder input) {
        int off = 0, // 整体字符串偏移量
                cLen; // 解析字符的长度
        if (suffixLength != 0) {
            int ssi;
            while ((off = input.indexOf(prefix, off)) >= 0) {
                ssi = input.indexOf(suffix, off);
                if (ssi < 0) {
                    break;
                }
                cLen = ssi - off - prefixLength;
                if (cLen != 4 && cLen != 6 && cLen != 8) {
                    // not unicode
                    continue;
                }
                off += replaceUnicodeFromCodepoint(input, off, ssi + suffixLength, off + prefixLength, ssi);
            }
        } else {
            int p;
            while ((off = input.indexOf(prefix, off)) >= 0) {
                for (p = off + prefixLength, cLen = 0;
                     cLen <= 8 && p < input.length() && Whether.hexChar(input.codePointAt(p));
                     p++) {
                    cLen++;
                }
                if (cLen != 4 && cLen != 6 && cLen != 8) {
                    // not unicode
                    off += cLen;
                    continue;
                }
                off += replaceUnicodeFromCodepoint(input, off, p, off + prefixLength, p);
            }
        }
        return input;
    }

    /**
     * 将带有unicode转义代码解码
     *
     * @param input 输入字符串
     * @return 返回解码后的字符串
     */
    @Nonnull
    public String decode(@Nonnull String input) {
        return decode(new StringBuilder(input)).toString();
    }
}
