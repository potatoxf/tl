package pxf.tlx.codec.impl;


import pxf.tl.api.JavaEnvironment;
import pxf.tl.api.InstanceSupplier;
import pxf.tl.help.Whether;
import pxf.tl.lang.AsciiTableMatcher;
import pxf.tl.lang.TextBuilder;
import pxf.tl.util.ToolMath;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 摩斯密码
 *
 * @author potatoxf
 */
public enum MorseCoder {
    /**
     * 摩斯密码
     */
    _A(2, 0b0000_0001, 'A'),
    _B(4, 0b0000_1000, 'B'),
    _C(4, 0b0000_1010, 'C'),
    _D(3, 0b0000_0100, 'D'),
    _E(1, 0b0000_0000, 'E'),
    _F(4, 0b0000_0010, 'F'),
    _G(3, 0b0000_0110, 'G'),
    _H(4, 0b0000_0000, 'H'),
    _I(2, 0b0000_0000, 'I'),
    _J(4, 0b0000_0111, 'J'),
    _K(3, 0b0000_0101, 'K'),
    _L(4, 0b0000_0100, 'L'),
    _M(2, 0b0000_0011, 'M'),
    _N(2, 0b0000_0010, 'N'),
    _O(3, 0b0000_0111, 'O'),
    _P(4, 0b0000_0110, 'P'),
    _Q(4, 0b0000_1101, 'Q'),
    _R(3, 0b0000_0010, 'R'),
    _S(3, 0b0000_0000, 'S'),
    _T(1, 0b0000_0001, 'T'),
    _U(3, 0b0000_0001, 'U'),
    _V(4, 0b0000_0001, 'V'),
    _W(3, 0b0000_0011, 'W'),
    _X(4, 0b0000_1001, 'X'),
    _Y(4, 0b0000_1011, 'Y'),
    _Z(4, 0b0000_1100, 'Z'),
    _0(5, 0b0001_1111, '0'),
    _1(5, 0b0000_1111, '1'),
    _2(5, 0b0000_0111, '2'),
    _3(5, 0b0000_0011, '3'),
    _4(5, 0b0000_0001, '4'),
    _5(5, 0b0000_0000, '5'),
    _6(5, 0b0001_0000, '6'),
    _7(5, 0b0001_1000, '7'),
    _8(5, 0b0001_1100, '8'),
    _9(5, 0b0001_1110, '9'),
    /**
     * .
     */
    DOT(6, 0b0001_0101, '.'),
    /**
     * :
     */
    COLON(6, 0b0011_1000, ':'),
    /**
     * ,
     */
    COMMA(6, 0b0011_0011, ','),
    /**
     * ;
     */
    SEMICOLON(6, 0b0010_1010, ';'),
    /**
     * ?
     */
    QUESTION(6, 0b0000_1100, '?'),
    /**
     * =
     */
    EQUAL(5, 0b0001_0001, '='),
    /**
     * '
     */
    SINGLE_QUOTE(6, 0b0001_1110, '\''),
    /**
     * /
     */
    SLASH(5, 0b0001_0010, '/'),
    /**
     * !
     */
    EXCLAMATION(6, 0b0010_1011, '!'),
    /**
     * -
     */
    RUNG(6, 0b0010_0001, '-'),
    /**
     * _
     */
    UNDERLINE(6, 0b0000_1101, '_'),
    /**
     * "
     */
    DOUBLE_QUOTE(6, 0b0001_0010, '"'),
    /**
     * (
     */
    LEFT_BRACKET(5, 0b0001_0110, '('),
    /**
     * )
     */
    RIGHT_BRACKET(6, 0b0010_1101, ')'),
    /**
     * $
     */
    DOLLAR(7, 0b0000_1001, '$'),
    /**
     * &
     */
    LOGIC_AND(5, 0b0000_1000, '&'),
    /**
     * @
     */
    ART(6, 0b0001_1010, '@'),
    /**
     * 空白字符
     */
    EMPTY(0, 0b0000_0000, ' ');
    /**
     * 莫斯密码最大位数
     */
    private static final int MAX_MORSE_LENGTH = 8;
    /**
     * 莫斯密码支持字符
     */
    private static final InstanceSupplier<Map<Character, MorseCoder>> CHARACTERS = InstanceSupplier.of(() -> {
        MorseCoder[] values = MorseCoder.values();
        Map<Character, MorseCoder> map = new HashMap<>(values.length, 1);
        for (MorseCoder value : values) {
            map.put(value.valueChar, value);
        }
        return Collections.unmodifiableMap(map);
    });
    /**
     * 莫斯密码支持字符
     */
    private static final InstanceSupplier<Map<Byte, MorseCoder>> CODES = InstanceSupplier.of(() -> {
        MorseCoder[] values = MorseCoder.values();
        Map<Byte, MorseCoder> map = new HashMap<>(values.length, 1);
        for (MorseCoder value : values) {
            map.put(value.morseCodeValue, value);
        }
        return Collections.unmodifiableMap(map);
    });
    /**
     * 位数
     */
    private final byte bitDigit;
    /**
     * 莫斯密码的值
     */
    private final byte morseCodeValue;
    /**
     * 莫斯密码对应的字符
     */
    private final char valueChar;

    MorseCoder(int bitDigit, int morseCodeValue, char valueChar) {
        this.bitDigit = (byte) bitDigit;
        this.morseCodeValue = (byte) morseCodeValue;
        this.valueChar = valueChar;
    }

    /**
     * 解析莫斯密码
     *
     * @param ch 字符
     * @return {@code MorseCode}
     */
    public static MorseCoder parseMorseCode(char ch) {
        if (AsciiTableMatcher.isMatcherExceptAsciiChar(ch, AsciiTableMatcher.INVISIBLE_CHAR)) {
            return MorseCoder.EMPTY;
        }
        MorseCoder morseCoder = CHARACTERS.get().get(Character.toUpperCase(ch));
        if (morseCoder != null) {
            return morseCoder;
        }
        throw new NoSuchElementException("Don't exist the morse code of [" + ch + "]");
    }

    /**
     * 解析莫斯密码
     *
     * @param morseCode 摩斯密码
     * @return {@code MorseCode}
     */
    public static MorseCoder parseMorseCode(String morseCode) {
        if (Whether.empty(morseCode)) {
            return MorseCoder.EMPTY;
        }
        byte cv = MorseCoder.parseMorseCodeValue(morseCode);
        MorseCoder morseCoder = CODES.get().get(cv);
        if (morseCoder != null) {
            return morseCoder;
        }
        throw new NoSuchElementException("Don't exist the morse code of [" + morseCode + "]");
    }

    /**
     * 显示所有摩斯密码映射表
     *
     * @return 所有摩斯密码映射表
     */
    public static String displayMorseCode() {
        StringBuilder sb = new StringBuilder();
        MorseCoder[] morseCoders = MorseCoder.values();
        for (int i = 0; i < morseCoders.length; i++) {
            sb.append(morseCoders[i]);
            if ((i + 1) % 5 == 0) {
                sb.append(JavaEnvironment.LINE_SEPARATOR);
            }
        }
        return sb.toString();
    }

    /**
     * 解析莫斯密码
     *
     * @param morseCode 摩斯密码
     * @return {@code MorseCode的字节值}
     */
    private static byte parseMorseCodeValue(String morseCode) {
        int length = morseCode.length();
        if (length == 0 || length > MAX_MORSE_LENGTH) {
            throw new IllegalArgumentException("The character length of morse code is illegal");
        }
        int codeValue = 0;
        for (int i = 0; i < length; i++) {
            char c = morseCode.charAt(i);
            codeValue = codeValue << 1;
            if (c == '-') {
                codeValue = codeValue ^ 1;
            } else {
                if (c != '.') {
                    throw new IllegalArgumentException("Only '.' and '-' characters are allowed");
                }
            }
        }
        return (byte) (codeValue & 0xFF);
    }

    /**
     * 在莫斯密码内是否包括字符
     *
     * @param c 字符
     * @return 如果包含返回true，否则返回false
     */
    public static boolean contains(char c) {
        return CHARACTERS.get().containsKey(c);
    }

    /**
     * 转为莫斯密码的字符
     *
     * @return {@code String}
     */
    public String toMorseCode() {
        if (bitDigit == 0) {
            return "";
        }
        TextBuilder sb = TextBuilder.of(bitDigit);
        for (int i = bitDigit; i > 0; i--) {
            boolean v = ToolMath.extractBitVal(morseCodeValue, i);
            if (v) {
                sb.append('-');
            } else {
                sb.append('.');
            }
        }
        return sb.toString();
    }

    /**
     * 转为{@code char}
     *
     * @return {@code String}
     */
    public char toCharacterValue() {
        return valueChar;
    }

    /**
     * 转为{@code String}
     *
     * @return {@code String}
     */
    public String toStringValue() {
        return String.valueOf(valueChar);
    }

    @Override
    public String toString() {
        return toCharacterValue() + " [" + String.format("%-8s", toMorseCode()) + "]";
    }
}
