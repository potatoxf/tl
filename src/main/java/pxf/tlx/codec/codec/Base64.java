package pxf.tlx.codec.codec;


import pxf.tl.api.Charsets;
import pxf.tl.util.ToolString;
import pxf.tlx.codec.impl.Base64Coder;

/**
 * Base64工具类，提供Base64的编码和解码方案<br>
 * base64编码是用64（2的6次方）个ASCII字符来表示256（2的8次方）个ASCII字符，<br>
 * 也就是三位二进制数组经过编码后变为四位的ASCII字符显示，长度比原来增加1/3。
 *
 * @author potatoxf
 */
public class Base64 {

    private static final Charsets DEFAULT_CHARSET = Charsets.UTF_8;
    // -------------------------------------------------------------------- encode

    /**
     * 编码为Base64，非URL安全的
     *
     * @param arr     被编码的数组
     * @param lineSep 在76个char之后是CRLF还是EOF
     * @return 编码后的bytes
     */
    public static byte[] encode(byte[] arr, boolean lineSep) {
        return lineSep
                ? Base64Coder.MIME.encodeTarget(arr)
                : Base64Coder.STRAND.encodeTarget(arr);
    }


    /**
     * base64编码
     *
     * @param source 被编码的base64字符串
     * @return 被加密后的字符串
     */
    public static String encode(byte[] source) {
        return Base64Coder.STRAND.encodeFromTarget(source);
    }


    /**
     * base64编码,URL安全的
     *
     * @param source 被编码的base64字符串
     * @return 被加密后的字符串
     */
    public static String encodeUrlSafe(byte[] source) {
        return Base64Coder.STRAND.encodeFromTarget(source);
    }


    // -------------------------------------------------------------------- decode

    /**
     * base64解码
     *
     * @param base64 被解码的base64字符串
     * @return 解码后的bytes
     */
    public static byte[] decode(CharSequence base64) {
        return Base64Decoder.decode(base64);
    }

    /**
     * 检查是否为Base64
     *
     * @param base64 Base64的bytes
     * @return 是否为Base64
     */
    public static boolean isBase64(CharSequence base64) {
        if (base64 == null || base64.length() < 2) {
            return false;
        }

        final byte[] bytes = ToolString.utf8Bytes(base64);

        if (bytes.length != base64.length()) {
            // 如果长度不相等，说明存在双字节字符，肯定不是Base64，直接返回false
            return false;
        }

        return isBase64(bytes);
    }

    /**
     * 检查是否为Base64
     *
     * @param base64Bytes Base64的bytes
     * @return 是否为Base64
     */
    public static boolean isBase64(byte[] base64Bytes) {
        if (base64Bytes == null || base64Bytes.length < 3) {
            return false;
        }
        boolean hasPadding = false;
        for (byte base64Byte : base64Bytes) {
            if (hasPadding) {
                if ('=' != base64Byte) {
                    // 前一个字符是'='，则后边的字符都必须是'='，即'='只能都位于结尾
                    return false;
                }
            } else if ('=' == base64Byte) {
                // 发现'=' 标记之
                hasPadding = true;
            } else if (!(Base64Decoder.isBase64Code(base64Byte) || isWhiteSpace(base64Byte))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isWhiteSpace(byte byteToCheck) {
        return switch (byteToCheck) {
            case ' ', '\n', '\r', '\t' -> true;
            default -> false;
        };
    }
}
