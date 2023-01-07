package pxf.tl.math;

import pxf.tl.api.Charsets;
import pxf.tl.api.PoolOfObject;
import pxf.tl.help.New;

import javax.annotation.Nonnull;

/**
 * @author potatoxf
 */
public final class Hex {

    public static final byte[] HEX_TABLE = new byte[]{
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70
    };

    /**
     * 判断给定字符串是否为16进制数<br>
     * 如果是，需要使用对应数字类型对象的{@code decode}方法解码<br>
     * 例如：{@code Integer.decode}方法解码int类型的16进制数字
     *
     * @param value 值
     * @return 是否为16进制
     */
    public static boolean isHexNumber(String value) {
        final int index = (value.startsWith("-") ? 1 : 0);
        if (value.startsWith("0x", index)
                || value.startsWith("0X", index)
                || value.startsWith("#", index)) {
            try {
                //noinspection ResultOfMethodCallIgnored
                Long.decode(value);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }

        return false;
    }

    //------------------------------------------------------------------------------------------------------------------
    //HEX转基本类型数组
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] decodeToChars(String values) {
        return decodeToChars(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] decodeToBytes(String values) {
        return decodeToBytes(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static short[] decodeToShorts(String values) {
        return decodeToShorts(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static int[] decodeToInts(String values) {
        return decodeToInts(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static long[] decodeToLongs(String values) {
        return decodeToLongs(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] decodeToChars(char... values) {
        return decodeToChars(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] decodeToBytes(char... values) {
        return decodeToBytes(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static short[] decodeToShorts(char... values) {
        return decodeToShorts(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static int[] decodeToInts(char... values) {
        return decodeToInts(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static long[] decodeToLongs(char... values) {
        return decodeToLongs(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] decodeToChars(byte... values) {
        return decodeToChars(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] decodeToBytes(byte... values) {
        return decodeToBytes(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static short[] decodeToShorts(byte... values) {
        return decodeToShorts(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static int[] decodeToInts(byte... values) {
        return decodeToInts(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static long[] decodeToLongs(byte... values) {
        return decodeToLongs(false, values);
    }

    //------------------------------------------------------------------------------------------------------------------
    //HEX转基本类型
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char decodeToChar(String value) {
        return decodeToChar(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte decodeToByte(String value) {
        return decodeToByte(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static short decodeToShort(String value) {
        return decodeToShort(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static int decodeToInt(String value) {
        return decodeToInt(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static long decodeToLong(String value) {
        return decodeToLong(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char decodeToChar(char[] value) {
        return decodeToChar(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte decodeToByte(char[] value) {
        return decodeToByte(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static short decodeToShort(char[] value) {
        return decodeToShort(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static int decodeToInt(char[] value) {
        return decodeToInt(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static long decodeToLong(char[] value) {
        return decodeToLong(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char decodeToChar(byte[] value) {
        return decodeToChar(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte decodeToByte(byte[] value) {
        return decodeToByte(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static short decodeToShort(byte[] value) {
        return decodeToShort(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static int decodeToInt(byte[] value) {
        return decodeToInt(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static long decodeToLong(byte[] value) {
        return decodeToLong(value, false);
    }


    //------------------------------------------------------------------------------------------------------------------
    //HEX转基本类型数组
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] decodeToChars(boolean isLowFirst, String values) {
        return decodeToChars(isLowFirst, values.toCharArray());
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] decodeToBytes(boolean isLowFirst, String values) {
        return decodeToBytes(isLowFirst, values.toCharArray());
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static short[] decodeToShorts(boolean isLowFirst, String values) {
        return decodeToShorts(isLowFirst, values.toCharArray());
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static int[] decodeToInts(boolean isLowFirst, String values) {
        return decodeToInts(isLowFirst, values.toCharArray());
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static long[] decodeToLongs(boolean isLowFirst, String values) {
        return decodeToLongs(isLowFirst, values.toCharArray());
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] decodeToChars(boolean isLowFirst, char... values) {
        int len = 4;
        if (values.length % len != 0) {
            throw new IllegalArgumentException("The bytes array length must " + len + " multiple");
        }
        char[] results = new char[values.length / len];
        char[] temp = new char[len];
        for (int i = 0; i < results.length; i++) {
            System.arraycopy(values, i * len, temp, 0, len);
            results[i] = decodeToChar(temp, isLowFirst);
        }
        return results;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] decodeToBytes(boolean isLowFirst, char... values) {
        int len = 4;
        if (values.length % len != 0) {
            throw new IllegalArgumentException("The bytes array length must " + len + " multiple");
        }
        byte[] results = new byte[values.length / len];
        char[] temp = new char[len];
        for (int i = 0; i < results.length; i++) {
            System.arraycopy(values, i * len, temp, 0, len);
            results[i] = decodeToByte(temp, isLowFirst);
        }
        return results;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static short[] decodeToShorts(boolean isLowFirst, char... values) {
        int len = 4;
        if (values.length % len != 0) {
            throw new IllegalArgumentException("The bytes array length must " + len + " multiple");
        }
        short[] results = new short[values.length / len];
        char[] temp = new char[len];
        for (int i = 0; i < results.length; i++) {
            System.arraycopy(values, i * len, temp, 0, len);
            results[i] = decodeToShort(temp, isLowFirst);
        }
        return results;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static int[] decodeToInts(boolean isLowFirst, char... values) {
        int len = 8;
        if (values.length % len != 0) {
            throw new IllegalArgumentException("The bytes array length must " + len + " multiple");
        }
        int[] results = new int[values.length / len];
        char[] temp = new char[len];
        for (int i = 0; i < results.length; i++) {
            System.arraycopy(values, i * len, temp, 0, len);
            results[i] = decodeToInt(temp, isLowFirst);
        }
        return results;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static long[] decodeToLongs(boolean isLowFirst, char... values) {
        int len = 16;
        if (values.length % len != 0) {
            throw new IllegalArgumentException("The bytes array length must " + len + " multiple");
        }
        long[] results = new long[values.length / len];
        char[] temp = new char[len];
        for (int i = 0; i < results.length; i++) {
            System.arraycopy(values, i * len, temp, 0, len);
            results[i] = decodeToLong(temp, isLowFirst);
        }
        return results;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] decodeToChars(boolean isLowFirst, byte... values) {
        int len = 4;
        if (values.length % len != 0) {
            throw new IllegalArgumentException("The bytes array length must " + len + " multiple");
        }
        char[] results = new char[values.length / len];
        byte[] temp = new byte[len];
        for (int i = 0; i < results.length; i++) {
            System.arraycopy(values, i * len, temp, 0, len);
            results[i] = decodeToChar(temp, isLowFirst);
        }
        return results;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] decodeToBytes(boolean isLowFirst, byte... values) {
        int len = 2;
        if (values.length % len != 0) {
            throw new IllegalArgumentException("The bytes array length must " + len + " multiple");
        }
        byte[] results = new byte[values.length / len];
        byte[] temp = new byte[len];
        for (int i = 0; i < results.length; i++) {
            System.arraycopy(values, i * len, temp, 0, len);
            results[i] = decodeToByte(temp, isLowFirst);
        }
        return results;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static short[] decodeToShorts(boolean isLowFirst, byte... values) {
        int len = 4;
        if (values.length % len != 0) {
            throw new IllegalArgumentException("The bytes array length must " + len + " multiple");
        }
        short[] results = new short[values.length / len];
        byte[] temp = new byte[len];
        for (int i = 0; i < results.length; i++) {
            System.arraycopy(values, i * len, temp, 0, len);
            results[i] = decodeToShort(temp, isLowFirst);
        }
        return results;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static int[] decodeToInts(boolean isLowFirst, byte... values) {
        int len = 8;
        if (values.length % len != 0) {
            throw new IllegalArgumentException("The bytes array length must " + len + " multiple");
        }
        int[] results = new int[values.length / len];
        byte[] temp = new byte[len];
        for (int i = 0; i < results.length; i++) {
            System.arraycopy(values, i * len, temp, 0, len);
            results[i] = decodeToInt(temp, isLowFirst);
        }
        return results;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static long[] decodeToLongs(boolean isLowFirst, byte... values) {
        int len = 16;
        if (values.length % len != 0) {
            throw new IllegalArgumentException("The bytes array length must " + len + " multiple");
        }
        long[] results = new long[values.length / len];
        byte[] temp = new byte[len];
        for (int i = 0; i < results.length; i++) {
            System.arraycopy(values, i * len, temp, 0, len);
            results[i] = decodeToLong(temp, isLowFirst);
        }
        return results;
    }

    //------------------------------------------------------------------------------------------------------------------
    //HEX转基本类型
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char decodeToChar(String value, boolean isLowFirst) {
        return decodeToChar(value.toCharArray(), isLowFirst);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte decodeToByte(String value, boolean isLowFirst) {
        return decodeToByte(value.toCharArray(), isLowFirst);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static short decodeToShort(String value, boolean isLowFirst) {
        return decodeToShort(value.toCharArray(), isLowFirst);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static int decodeToInt(String value, boolean isLowFirst) {
        return decodeToInt(value.toCharArray(), isLowFirst);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static long decodeToLong(String value, boolean isLowFirst) {
        return decodeToLong(value.toCharArray(), isLowFirst);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char decodeToChar(char[] value, boolean isLowFirst) {
        return (char) decode(value, isLowFirst, char.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte decodeToByte(char[] value, boolean isLowFirst) {
        return (byte) decode(value, isLowFirst, byte.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static short decodeToShort(char[] value, boolean isLowFirst) {
        return (short) decode(value, isLowFirst, short.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static int decodeToInt(char[] value, boolean isLowFirst) {
        return (int) decode(value, isLowFirst, int.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static long decodeToLong(char[] value, boolean isLowFirst) {
        return decode(value, isLowFirst, long.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    private static long decode(char[] value, boolean isLowFirst, @Nonnull Class<?> type) {
        if (value.length != PoolOfObject.PRIMITIVE_BYTE_LENGTH.get().get(type) << 1) {
            throw new IllegalArgumentException();
        }
        long result = 0;
        if (isLowFirst) {
            for (int i = 0; i < value.length; i++) {
                result |= (long) Character.digit(value[i], 16) << (i << 2);
            }
        } else {
            for (int i = 0; i < value.length; i++) {
                result |= (long) Character.digit(value[i], 16) << (value.length - i - 1 << 2);
            }
        }
        return result;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char decodeToChar(byte[] value, boolean isLowFirst) {
        return (char) decode(value, isLowFirst, char.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte decodeToByte(byte[] value, boolean isLowFirst) {
        return (byte) decode(value, isLowFirst, byte.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static short decodeToShort(byte[] value, boolean isLowFirst) {
        return (short) decode(value, isLowFirst, short.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static int decodeToInt(byte[] value, boolean isLowFirst) {
        return (int) decode(value, isLowFirst, int.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static long decodeToLong(byte[] value, boolean isLowFirst) {
        return decode(value, isLowFirst, long.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    private static long decode(byte[] value, boolean isLowFirst, @Nonnull Class<?> type) {
        if (value.length != PoolOfObject.PRIMITIVE_BYTE_LENGTH.get().get(type) << 1) {
            throw new IllegalArgumentException();
        }
        long result = 0;
        if (isLowFirst) {
            for (int i = 0; i < value.length; i++) {
                result |= (long) Character.digit(value[i], 16) << (i << 2);
            }
        } else {
            for (int i = 0; i < value.length; i++) {
                result |= (long) Character.digit(value[i], 16) << (value.length - i - 1 << 2);
            }
        }
        return result;
    }

    //------------------------------------------------------------------------------------------------------------------
    //字符串类型转HEX
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value    值
     * @param charsets 字符集
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(@Nonnull CharSequence value, @Nonnull Charsets charsets) {
        return encodeToChars(value, charsets, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value    值
     * @param charsets 字符集
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(@Nonnull CharSequence value, @Nonnull Charsets charsets) {
        return encodeToString(value, charsets, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value    值
     * @param charsets 字符集
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(@Nonnull CharSequence value, @Nonnull Charsets charsets) {
        return encodeToBytes(value, charsets, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param charsets   字符集
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(@Nonnull CharSequence value, @Nonnull Charsets charsets, boolean isLowFirst) {
        return encodeToChars(isLowFirst, New.bytes(value.toString(), charsets));
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param charsets   字符集
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(@Nonnull CharSequence value, @Nonnull Charsets charsets, boolean isLowFirst) {
        return encodeToString(isLowFirst, New.bytes(value.toString(), charsets));
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param charsets   字符集
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(@Nonnull CharSequence value, @Nonnull Charsets charsets, boolean isLowFirst) {
        return encodeToBytes(isLowFirst, New.bytes(value.toString(), charsets));
    }

    //------------------------------------------------------------------------------------------------------------------
    //数组基本类型转HEX
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(char... values) {
        return encodeToChars(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(byte... values) {
        return encodeToChars(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(short... values) {
        return encodeToChars(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(int... values) {
        return encodeToChars(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(long... values) {
        return encodeToChars(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(char... values) {
        return encodeToString(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(byte... values) {
        return encodeToString(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(short... values) {
        return encodeToString(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(int... values) {
        return encodeToString(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(long... values) {
        return encodeToString(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(char... values) {
        return encodeToBytes(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(byte... values) {
        return encodeToBytes(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(short... values) {
        return encodeToBytes(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(int... values) {
        return encodeToBytes(false, values);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param values 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(long... values) {
        return encodeToBytes(false, values);
    }

    //------------------------------------------------------------------------------------------------------------------
    //基本类型转HEX
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(char value) {
        return encodeToChars(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(byte value) {
        return encodeToChars(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(short value) {
        return encodeToChars(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(int value) {
        return encodeToChars(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(long value) {
        return encodeToChars(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(char value) {
        return encodeToString(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(byte value) {
        return encodeToString(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(short value) {
        return encodeToString(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(int value) {
        return encodeToString(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(long value) {
        return encodeToString(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(char value) {
        return encodeToBytes(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(byte value) {
        return encodeToBytes(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(short value) {
        return encodeToBytes(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(int value) {
        return encodeToBytes(value, false);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value 值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(long value) {
        return encodeToBytes(value, false);
    }


    //------------------------------------------------------------------------------------------------------------------
    //数组基本类型转HEX
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(boolean isLowFirst, char... values) {
        return encodeToString(isLowFirst, values).toCharArray();
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(boolean isLowFirst, byte... values) {
        return encodeToString(isLowFirst, values).toCharArray();
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(boolean isLowFirst, short... values) {
        return encodeToString(isLowFirst, values).toCharArray();
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(boolean isLowFirst, int... values) {
        return encodeToString(isLowFirst, values).toCharArray();
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(boolean isLowFirst, long... values) {
        return encodeToString(isLowFirst, values).toCharArray();
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(boolean isLowFirst, char... values) {
        return new String(encodeToBytes(isLowFirst, values));
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(boolean isLowFirst, byte... values) {
        return new String(encodeToBytes(isLowFirst, values));
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(boolean isLowFirst, short... values) {
        return new String(encodeToBytes(isLowFirst, values));
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(boolean isLowFirst, int... values) {
        return new String(encodeToBytes(isLowFirst, values));
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(boolean isLowFirst, long... values) {
        return new String(encodeToBytes(isLowFirst, values));
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(boolean isLowFirst, char... values) {
        int i = 0, len = 4;
        byte[] results = new byte[values.length * len];
        for (char value : values) {
            System.arraycopy(encodeToBytes(value, false), 0, results, i, len);
            i += len;
        }
        return results;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(boolean isLowFirst, byte... values) {
        int i = 0, len = 2;
        byte[] results = new byte[values.length * len];
        for (byte value : values) {
            System.arraycopy(encodeToBytes(value, isLowFirst), 0, results, i, len);
            i += len;
        }
        return results;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(boolean isLowFirst, short... values) {
        int i = 0, len = 4;
        byte[] results = new byte[values.length * len];
        for (short value : values) {
            System.arraycopy(encodeToBytes(value, isLowFirst), 0, results, i, len);
            i += len;
        }
        return results;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(boolean isLowFirst, int... values) {
        int i = 0, len = 8;
        byte[] results = new byte[values.length * len];
        for (int value : values) {
            System.arraycopy(encodeToBytes(value, isLowFirst), 0, results, i, len);
            i += len;
        }
        return results;
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param isLowFirst 是否低位在前
     * @param values     值
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(boolean isLowFirst, long... values) {
        int i = 0, len = 16;
        byte[] results = new byte[values.length * len];
        for (long value : values) {
            System.arraycopy(encodeToBytes(value, isLowFirst), 0, results, i, len);
            i += len;
        }
        return results;
    }

    //------------------------------------------------------------------------------------------------------------------
    //基本类型转HEX
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(char value, boolean isLowFirst) {
        return encodeToString(value, isLowFirst).toCharArray();
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(byte value, boolean isLowFirst) {
        return encodeToString(value, isLowFirst).toCharArray();
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(short value, boolean isLowFirst) {
        return encodeToString(value, isLowFirst).toCharArray();
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(int value, boolean isLowFirst) {
        return encodeToString(value, isLowFirst).toCharArray();
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static char[] encodeToChars(long value, boolean isLowFirst) {
        return encodeToString(value, isLowFirst).toCharArray();
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(char value, boolean isLowFirst) {
        return new String(encodeToBytes(value, isLowFirst));
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(byte value, boolean isLowFirst) {
        return new String(encodeToBytes(value, isLowFirst));
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(short value, boolean isLowFirst) {
        return new String(encodeToBytes(value, isLowFirst));
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(int value, boolean isLowFirst) {
        return new String(encodeToBytes(value, isLowFirst));
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static String encodeToString(long value, boolean isLowFirst) {
        return new String(encodeToBytes(value, isLowFirst));
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(char value, boolean isLowFirst) {
        return encodeToBytes(value, isLowFirst, char.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(byte value, boolean isLowFirst) {
        return encodeToBytes(value, isLowFirst, byte.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(short value, boolean isLowFirst) {
        return encodeToBytes(value, isLowFirst, short.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(int value, boolean isLowFirst) {
        return encodeToBytes(value, isLowFirst, int.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    public static byte[] encodeToBytes(long value, boolean isLowFirst) {
        return encodeToBytes(value, isLowFirst, long.class);
    }

    /**
     * 将 {@code 0-16}数字转为16进制 {@code 0-9A-F}字符
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @param type       类型
     * @return 返回16进制 {@code 0-9A-F}字符
     */
    private static byte[] encodeToBytes(long value, boolean isLowFirst, @Nonnull Class<?> type) {
        int halfByteCount = PoolOfObject.PRIMITIVE_BYTE_LENGTH.get().get(type) << 1;
        byte[] results = new byte[halfByteCount];
        for (int i = 0; i < halfByteCount; i++) {
            results[!isLowFirst ? halfByteCount - i - 1 : i] = HEX_TABLE[(int) ((value >> (i << 2)) & 0xFL)];
        }
        return results;
    }
}
