package pxf.tl.text.escaper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Html转义器
 *
 * @author potatoxf
 */
public final class HtmlEscaper {

    private static final String HTML_ESCAPE_PREFIX = "&";
    private static final int PL = HTML_ESCAPE_PREFIX.length();
    private static final String HTML_ESCAPE_SUFFIX = ";";
    private static final int SL = HTML_ESCAPE_PREFIX.length();
    private static final int ENTITY_LENGTH = 7;
    private static final Map<Character, String> ENTITIES_ENCODE_MAPPING;
    private static final Map<String, Character> ENTITIES_DECODE_MAPPING;

    static {
        Map<Character, String> map = new HashMap<>();
        map.put(' ', "emsp");
        map.put(' ', "ensp");
        map.put(' ', "nbsp");
        map.put('´', "acute");
        map.put('©', "copy");
        map.put('>', "gt");
        map.put('µ', "micro");
        map.put('®', "reg");
        map.put('&', "amp");
        map.put('°', "deg");
        map.put('¡', "iexcl");
        map.put('»', "raquo");
        map.put('¦', "brvbar");
        map.put('÷', "divide");
        map.put('¿', "iquest");
        map.put('¬', "not");
        map.put('§', "sect");
        map.put('•', "bull");
        map.put('½', "frac12");
        map.put('«', "laquo");
        map.put('¶', "para");
        map.put('¨', "uml");
        map.put('¸', "cedil");
        map.put('¼', "frac14");
        map.put('<', "lt");
        map.put('±', "plusmn");
        map.put('×', "times");
        map.put('¢', "cent");
        map.put('¾', "frac34");
        map.put('¯', "macr");
        map.put('“', "quot");
        map.put('™', "trade");
        map.put('€', "euro");
        map.put('£', "pound");
        map.put('¥', "yen");
        map.put('„', "bdquo");
        map.put('…', "hellip");
        map.put('·', "middot");
        map.put('›', "rsaquo");
        map.put('ª', "ordf");
        map.put('ˆ', "circ");
        map.put('—', "mdash");
        map.put('’', "rsquo");
        map.put('º', "ordm");
        map.put('†', "dagger");
        map.put('‹', "lsaquo");
        map.put('–', "ndash");
        map.put('‚', "sbquo");
        map.put('”', "rdquo");
        map.put('‘', "lsquo");
        map.put('‰', "permil");
        map.put('˜', "tilde");
        map.put('≈', "asymp");
        map.put('⁄', "frasl");
        map.put('←', "larr");
        map.put('∂', "part");
        map.put('♠', "spades");
        map.put('∩', "cap");
        map.put('≥', "ge");
        map.put('≤', "le");
        map.put('′', "prime");
        map.put('∑', "sum");
        map.put('♣', "clubs");
        map.put('↔', "harr");
        map.put('◊', "loz");
        map.put('↑', "uarr");
        map.put('↓', "darr");
        map.put('♥', "hearts");
        map.put('−', "minus");
        map.put('∏', "prod");
        map.put('‍', "zwj");
        map.put('♦', "diams");
        map.put('∞', "infin");
        map.put('≠', "ne");
        map.put('√', "radic");
        map.put('≡', "equiv");
        map.put('∫', "int");
        map.put('‾', "oline");
        map.put('→', "rarr");
        map.put('α', "alpha");
        map.put('η', "eta");
        map.put('μ', "mu");
        map.put('π', "pi");
        map.put('θ', "theta");
        map.put('β', "beta");
        map.put('γ', "gamma");
        map.put('ν', "nu");
        map.put('ψ', "psi");
        map.put('υ', "upsilon");
        map.put('χ', "chi");
        map.put('ι', "iota");
        map.put('ω', "omega");
        map.put('ρ', "rho");
        map.put('ξ', "xi");
        map.put('δ', "delta");
        map.put('κ', "kappa");
        map.put('ο', "omicron");
        map.put('σ', "sigma");
        map.put('ζ', "zeta");
        map.put('ε', "epsilon");
        map.put('λ', "lambda");
        map.put('φ', "phi");
        map.put('τ', "tau");
        map.put('ς', "sigmaf");

        Map<Character, String> encodeMap = new HashMap<Character, String>(map.size(), 1);
        encodeMap.putAll(map);
        ENTITIES_ENCODE_MAPPING = Collections.unmodifiableMap(encodeMap);
        Map<String, Character> decodeMap = new HashMap<String, Character>(map.size(), 1);
        for (Map.Entry<Character, String> entry : map.entrySet()) {
            decodeMap.put(entry.getValue(), entry.getKey());
        }
        ENTITIES_DECODE_MAPPING = Collections.unmodifiableMap(decodeMap);
    }

    /**
     * 获取解码字符
     *
     * @param encodeString 编码后字符串
     * @return 返回编码后的字符串
     */
    public Character getDecode(String encodeString) {
        return ENTITIES_DECODE_MAPPING.get(encodeString);
    }

    /**
     * 获取编码字符串
     *
     * @param ch 解码后的字符
     * @return 返回编码后的字符串
     */
    public String getEncode(char ch) {
        return ENTITIES_ENCODE_MAPPING.get(ch);
    }

    /**
     * 获取编码字符串
     *
     * @param codepoint unicode码点
     * @return 返回编码后的字符串
     */
    public String getEncode(int codepoint) {
        char[] chars = Character.toChars(codepoint);
        if (chars.length == 1) {
            return ENTITIES_ENCODE_MAPPING.get(chars[0]);
        }
        return null;
    }

    /**
     * 将html特殊字符编码成转义代码
     *
     * @param input 输入字符串
     */
    public StringBuilder encode(StringBuilder input) {
        String encodeString;
        for (int i = 0; i < input.length(); i++) {
            int codepoint = input.codePointAt(i);
            char[] chars = Character.toChars(codepoint);
            if (chars.length == 1 && (encodeString = getEncode(chars[0])) != null) {
                input.deleteCharAt(i);
                input.insert(i++, "&");
                input.insert(i, encodeString);
                i += encodeString.length();
                input.insert(i, ";");
            }
        }
        return input;
    }

    /**
     * 将html特殊字符编码成转义代码
     *
     * @param input 输入字符串
     * @return 返回编码后的字符串
     */
    public String encode(String input) {
        int length = input.length();
        StringBuilder sb = new StringBuilder(length * 4);
        String encodeString;
        for (int i = 0; i < length; i++) {
            int codepoint = input.codePointAt(i);
            char[] chars = Character.toChars(codepoint);
            if (chars.length == 1 && (encodeString = getEncode(chars[0])) != null) {
                sb.append("&").append(encodeString).append(";");
            } else {
                sb.append(chars);
            }
        }
        return sb.toString();
    }

    /**
     * 将带有html特殊字符转义代码解码
     *
     * @param input 输入字符串
     */
    public StringBuilder decode(StringBuilder input) {
        int psi = 0, ssi;
        while ((psi = input.indexOf(HTML_ESCAPE_PREFIX, psi)) >= 0) {
            ssi = input.indexOf(HTML_ESCAPE_SUFFIX, psi + PL);
            if (ssi < 0) {
                break;
            }
            if (ssi - psi - PL > ENTITY_LENGTH) {
                continue;
            }
            Character decodeCharacter = getDecode(input.substring(psi + PL, ssi));
            if (decodeCharacter != null) {
                input.delete(psi, ssi + SL).insert(psi, decodeCharacter);
                // skip
                psi = ssi + SL;
            }
        }
        return input;
    }

    /**
     * 将带有html特殊字符转义代码解码
     *
     * @param input 输入字符串
     * @return 返回解码后的字符串
     */
    public String decode(String input) {
        int length = input.length();
        StringBuilder sb = new StringBuilder(length);
        int psi = 0, ssi, asi = 0;
        while ((psi = input.indexOf(HTML_ESCAPE_PREFIX, psi)) >= 0) {
            sb.append(input, asi, psi);
            asi += psi;
            ssi = input.indexOf(HTML_ESCAPE_SUFFIX, psi + PL);
            if (ssi < 0) {
                break;
            }
            if (ssi - psi - PL > ENTITY_LENGTH) {
                continue;
            }
            Character decodeCharacter = getDecode(input.substring(psi + PL, ssi));
            if (decodeCharacter != null) {
                sb.append(decodeCharacter);
                // skip
                psi = ssi + SL;
                asi = psi;
            }
        }
        if (asi != length) {
            sb.append(input, asi, length);
        }
        return sb.toString();
    }
}
