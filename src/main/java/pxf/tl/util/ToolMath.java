package pxf.tl.util;

import pxf.tl.api.PoolOfCommon;
import pxf.tl.api.PoolOfObject;
import pxf.tl.math.Arrangement;
import pxf.tl.math.Combination;
import pxf.tl.math.Money;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * 数学助手类
 *
 * @author potatoxf
 */
public final class ToolMath {
    private ToolMath() throws IllegalAccessException {
        throw new IllegalAccessException(
                "The instance creation is not allowed,because this is static method utils class");
    }

    /**
     * Returns a power of two size for the given target number.
     *
     * @param num target number
     * @param max max number,must be greater 0
     */
    public static int floorPower2(int num, int max) {
        int n = num - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= max) ? max : n + 1;
    }

    /**
     * Calculate avg of a list of numbers
     *
     * @param numbers array to store numbers
     * @return mean of given numbers
     */
    public static double avg(double... numbers) {
        double sum = 0;
        for (double number : numbers) {
            sum += number;
        }
        return sum / numbers.length;
    }

    /**
     * find avg value of int array
     *
     * @param array the array contains element and the sum does not excess long value limit
     * @return avg value
     */
    public static double avg(int... array) {
        double sum = 0;
        for (int value : array) {
            sum += value;
        }
        return sum / array.length;
    }

    /**
     * get greatest common divisor
     *
     * @param num1 the first number
     * @param num2 the second number
     * @return gcd
     */
    public static int gcd(int num1, int num2) {
        if (num1 < 0 || num2 < 0) {
            throw new ArithmeticException();
        }

        if (num1 == 0 || num2 == 0) {
            return Math.abs(num1 - num2);
        }

        while (num1 % num2 != 0) {
            int remainder = num1 % num2;
            num1 = num2;
            num2 = remainder;
        }
        return num2;
    }

    /**
     * get greatest common divisor in array
     *
     * @param number contains number
     * @return gcd
     */
    public static int gcd(int... number) {
        int result = number[0];
        // call gcd function (input two value)
        for (int i = 1; i < number.length; i++) {
            result = gcd(result, number[i]);
        }
        return result;
    }

    /**
     * Calculate average median
     *
     * @param values number series
     * @return median of given {@code values}
     */
    public static double median(byte... values) {
        Arrays.sort(values);
        int length = values.length;
        return length % 2 == 0
                ? (values[length / 2] + values[length / 2 - 1]) / 2.0
                : values[length / 2];
    }

    /**
     * Calculate average median
     *
     * @param values number series
     * @return median of given {@code values}
     */
    public static double median(short... values) {
        Arrays.sort(values);
        int length = values.length;
        return length % 2 == 0
                ? (values[length / 2] + values[length / 2 - 1]) / 2.0
                : values[length / 2];
    }

    /**
     * Calculate average median
     *
     * @param values number series
     * @return median of given {@code values}
     */
    public static double median(int... values) {
        Arrays.sort(values);
        int length = values.length;
        return length % 2 == 0
                ? (values[length / 2] + values[length / 2 - 1]) / 2.0
                : values[length / 2];
    }

    /**
     * Calculate average median
     *
     * @param values number series
     * @return median of given {@code values}
     */
    public static double median(long... values) {
        Arrays.sort(values);
        int length = values.length;
        return length % 2 == 0
                ? (values[length / 2] + values[length / 2 - 1]) / 2.0
                : values[length / 2];
    }

    /**
     * Calculate average median
     *
     * @param values number series
     * @return median of given {@code values}
     */
    public static double median(float... values) {
        Arrays.sort(values);
        int length = values.length;
        return length % 2 == 0
                ? (values[length / 2] + values[length / 2 - 1]) / 2.0
                : values[length / 2];
    }

    /**
     * Calculate average median
     *
     * @param values number series
     * @return median of given {@code values}
     */
    public static double median(double... values) {
        Arrays.sort(values);
        int length = values.length;
        return length % 2 == 0
                ? (values[length / 2] + values[length / 2 - 1]) / 2.0
                : values[length / 2];
    }

    /**
     * Get mode
     *
     * @param values number series
     * @return mode of given {@code values}
     */
    public static byte mode(byte... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException();
        }
        if (values.length == 1) {
            return values[0];
        }
        int cnt = 0;
        byte m = values[0];
        for (byte value : values) {
            if (cnt == 0) {
                cnt = 1;
                m = value;
            } else if (m != value) {
                cnt--;
            } else {
                cnt++;
            }
        }
        return m;
    }

    /**
     * Get mode
     *
     * @param values number series
     * @return mode of given {@code values}
     */
    public static short mode(short... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException();
        }
        if (values.length == 1) {
            return values[0];
        }
        int cnt = 0;
        short m = values[0];
        for (short value : values) {
            if (cnt == 0) {
                cnt = 1;
                m = value;
            } else if (m != value) {
                cnt--;
            } else {
                cnt++;
            }
        }
        return m;
    }

    /**
     * Get mode
     *
     * @param values number series
     * @return mode of given {@code values}
     */
    public static int mode(int... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException();
        }
        if (values.length == 1) {
            return values[0];
        }
        int cnt = 0;
        int m = values[0];
        for (int value : values) {
            if (cnt == 0) {
                cnt = 1;
                m = value;
            } else if (m != value) {
                cnt--;
            } else {
                cnt++;
            }
        }
        return m;
    }

    /**
     * Get mode
     *
     * @param values number series
     * @return mode of given {@code values}
     */
    public static long mode(long... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException();
        }
        if (values.length == 1) {
            return values[0];
        }
        int cnt = 0;
        long m = values[0];
        for (long value : values) {
            if (cnt == 0) {
                cnt = 1;
                m = value;
            } else if (m != value) {
                cnt--;
            } else {
                cnt++;
            }
        }
        return m;
    }

    /**
     * Get mode
     *
     * @param values number series
     * @return mode of given {@code values}
     */
    public static float mode(float... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException();
        }
        if (values.length == 1) {
            return values[0];
        }
        int cnt = 0;
        float m = values[0];
        for (float value : values) {
            if (cnt == 0) {
                cnt = 1;
                m = value;
            } else if (m != value) {
                cnt--;
            } else {
                cnt++;
            }
        }
        return m;
    }

    /**
     * Get mode
     *
     * @param values number series
     * @return mode of given {@code values}
     */
    public static double mode(double... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException();
        }
        if (values.length == 1) {
            return values[0];
        }
        int cnt = 0;
        double m = values[0];
        for (double value : values) {
            if (cnt == 0) {
                cnt = 1;
                m = value;
            } else if (m != value) {
                cnt--;
            } else {
                cnt++;
            }
        }
        return m;
    }

    /**
     * Calculate combinations
     *
     * @param n first number
     * @param k second number
     * @return combinations of given {@code n} and {@code k}
     */
    public static long combinations(int n, int k) {
        if (n - k < 0) {
            throw new IllegalArgumentException("The n must greater than k and equal");
        }
        if (k > 0) {
            throw new IllegalArgumentException("number is negative");
        }
        return doFactorial(n) / (doFactorial(k) * doFactorial(n - k));
    }

    /**
     * Calculate factorial N using iteration
     *
     * @param number the number
     * @return the factorial of {@code number}
     */
    public static long factorial(int number) {
        return factorial((long) number);
    }

    /**
     * Calculate factorial N using iteration
     *
     * @param number the number
     * @return the factorial of {@code number}
     */
    public static long factorial(long number) {
        if (number < 0) {
            throw new IllegalArgumentException("number is negative");
        }
        return doFactorial(number);
    }

    /**
     * Calculate factorial N using iteration
     *
     * @param number the number
     * @return the factorial of {@code number}
     */
    private static long doFactorial(long number) {
        long factorial = 1;
        for (int i = 1; i <= number; ++i) {
            factorial = doMultiplyExact(factorial, i);
        }
        return factorial;
    }

    /**
     * Returns the product of the arguments, throwing an exception if the result overflows an {@code
     * int}.
     *
     * @param x the first value
     * @param y the second value
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     */
    private static long doMultiplyExact(long x, long y) {
        long r = x * y;
        if (((x | y) >>> 31 != 0)) {
            // Some bits greater than 2^31 that might cause overflow
            // Check the result using the divide operator
            // and check for the special case of Long.MIN_VALUE * -1
            if (((y != 0) && (r / y != x)) || (x == Long.MIN_VALUE && y == -1)) {
                throw new ArithmeticException("long overflow");
            }
        }
        x = r;
        return x;
    }

    /**
     * Calculate the surface area of a cube.
     *
     * @param sideLength side length of cube
     * @return surface area of given cube
     */
    public static double surfaceAreaCube(double sideLength) {
        return 6 * sideLength * sideLength;
    }

    /**
     * Calculate the surface area of a sphere.
     *
     * @param radius radius of sphere
     * @return surface area of given sphere
     */
    public static double surfaceAreaSphere(double radius) {
        return 4 * Math.PI * radius * radius;
    }

    /**
     * Calculate the area of a rectangle
     *
     * @param length length of rectangle
     * @param width  width of rectangle
     * @return area of given rectangle
     */
    public static double surfaceAreaRectangle(double length, double width) {
        return length * width;
    }

    /**
     * Calculate the area of a square
     *
     * @param sideLength side length of square
     * @return area of given square
     */
    public static double surfaceAreaSquare(double sideLength) {
        return sideLength * sideLength;
    }

    /**
     * Calculate the area of a triangle
     *
     * @param base   base of triangle
     * @param height height of triangle
     * @return area of given triangle
     */
    public static double surfaceAreaTriangle(double base, double height) {
        return base * height / 2;
    }

    /**
     * Calculate the area of a parallelogram
     *
     * @param base   base of parallelogram
     * @param height height of parallelogram
     * @return area of given parallelogram
     */
    public static double surfaceAreaParallelogram(double base, double height) {
        return base * height;
    }

    /**
     * Calculate the area of a trapezium
     *
     * @param base1  upper base of trapezium
     * @param base2  bottom base of trapezium
     * @param height height of trapezium
     * @return area of given trapezium
     */
    public static double surfaceAreaTrapezium(double base1, double base2, double height) {
        return (base1 + base2) * height / 2;
    }

    /**
     * Calculate the area of a circle
     *
     * @param radius radius of circle
     * @return area of given circle
     */
    public static double surfaceAreaCircle(double radius) {
        return Math.PI * radius * radius;
    }

    /**
     * Check if a,b,c are a Pythagorean Triple
     *
     * @param a x/y component length of a right triangle
     * @param b y/x component length of a right triangle
     * @param c hypotenuse length of a right triangle
     * @return boolean <tt>true</tt> if a, b, c satisfy the Pythagorean theorem, otherwise
     * <tt>false</tt>
     */
    public static boolean isPythagoreanTriple(int a, int b, int c) {
        if (a <= 0 || b <= 0 || c <= 0) {
            return false;
        } else {
            return (a * a) + (b * b) == (c * c);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //十六进制（简写为hex或下标16）在数学中是一种逢16进1的进位制，一般用数字0到9和字母A到F表示（其中:A~F即10~15）。<br>
    //例如十进制数57，在二进制写作111001，在16进制写作39。<br>
    //像java,c这样的语言为了区分十六进制和十进制数值,会在十六进制数的前面加上 0x,比如0x20是十进制的32,而不是十进制的20<br>
    //
    //<p>参考：https://my.oschina.net/xinxingegeya/blog/287476
    //------------------------------------------------------------------------------------------------------------------


    /**
     * 将{@link Color}编码为Hex形式
     *
     * @param color {@link Color}
     * @return Hex字符串
     */
    public static String encodeColor(Color color) {
        return encodeColor(color, "#");
    }

    /**
     * 将{@link Color}编码为Hex形式
     *
     * @param color  {@link Color}
     * @param prefix 前缀字符串，可以是#、0x等
     * @return Hex字符串
     */
    public static String encodeColor(Color color, String prefix) {
        final StringBuilder builder = new StringBuilder(prefix);
        String colorHex;
        colorHex = Integer.toHexString(color.getRed());
        if (1 == colorHex.length()) {
            builder.append('0');
        }
        builder.append(colorHex);
        colorHex = Integer.toHexString(color.getGreen());
        if (1 == colorHex.length()) {
            builder.append('0');
        }
        builder.append(colorHex);
        colorHex = Integer.toHexString(color.getBlue());
        if (1 == colorHex.length()) {
            builder.append('0');
        }
        builder.append(colorHex);
        return builder.toString();
    }

    /**
     * 将Hex颜色值转为
     *
     * @param hexColor 16进制颜色值，可以以#开头，也可以用0x开头
     * @return {@link Color}
     */
    public static Color decodeColor(String hexColor) {
        return Color.decode(hexColor);
    }

    /**
     * 将数值进行大小端头转换
     *
     * @param value 数值
     * @return 返回小头端数值
     */
    public static short reserveEndian(short value) {
        return (short) reserveEndian(value, 2);
    }

    /**
     * 将数值进行大小端头转换
     *
     * @param value 数值
     * @return 返回小头端数值
     */
    public static int reserveEndian(int value) {
        return (int) reserveEndian(value, 4);
    }

    /**
     * 将数值进行大小端头转换
     *
     * @param value 数值
     * @return 返回小头端数值
     */
    public static long reserveEndian(long value) {
        return reserveEndian(value, 8);
    }

    /**
     * 将数值进行大小端头转换
     *
     * @param value        数值
     * @param countByteNum 总字节数
     * @return 返回小头端数值
     */
    private static long reserveEndian(long value, int countByteNum) {

        long result = 0x00;

        int mask = 0xff;

        result |= value & mask;

        for (int i = 1; i < countByteNum; i++) {

            result <<= 8;
            mask <<= 8;
            result |= (value & mask) >> (8 * i);
        }

        return result;
    }

    /**
     * 计算排列数，即A(n, m) = n!/(n-m)!
     *
     * @param n 总数
     * @param m 选择的个数
     * @return 排列数
     */
    public static long arrangementCount(int n, int m) {
        return Arrangement.count(n, m);
    }

    /**
     * 计算排列数，即A(n, n) = n!
     *
     * @param n 总数
     * @return 排列数
     */
    public static long arrangementCount(int n) {
        return Arrangement.count(n);
    }

    /**
     * 排列选择（从列表中选择n个排列）
     *
     * @param datas 待选列表
     * @param m     选择个数
     * @return 所有排列列表
     */
    public static List<String[]> arrangementSelect(String[] datas, int m) {
        return new Arrangement(datas).select(m);
    }

    /**
     * 全排列选择（列表全部参与排列）
     *
     * @param datas 待选列表
     * @return 所有排列列表
     */
    public static List<String[]> arrangementSelect(String[] datas) {
        return new Arrangement(datas).select();
    }

    /**
     * 计算组合数，即C(n, m) = n!/((n-m)! * m!)
     *
     * @param n 总数
     * @param m 选择的个数
     * @return 组合数
     */
    public static long combinationCount(int n, int m) {
        return Combination.count(n, m);
    }

    /**
     * 组合选择（从列表中选择n个组合）
     *
     * @param datas 待选列表
     * @param m     选择个数
     * @return 所有组合列表
     */
    public static List<String[]> combinationSelect(String[] datas, int m) {
        return new Combination(datas).select(m);
    }

    /**
     * 金额元转换为分
     *
     * @param yuan 金额，单位元
     * @return 金额，单位分
     */
    public static long yuanToCent(double yuan) {
        return new Money(yuan).getCent();
    }

    /**
     * 金额分转换为元
     *
     * @param cent 金额，单位分
     * @return 金额，单位元
     */
    public static double centToYuan(long cent) {
        long yuan = cent / 100;
        int centPart = (int) (cent % 100);
        return new Money(yuan, centPart).getAmount().doubleValue();
    }

    /**
     * 转成最近被整除的被除数
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @return 返回可以被整除的被除数
     */
    public static int ceilingNum(int dividend, int divisor) {
        return (dividend / divisor) * divisor;
    }

    /**
     * 转成最近被整除的被除数
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @return 返回可以被整除的被除数
     */
    public static long ceilingNum(long dividend, long divisor) {
        return (dividend / divisor) * divisor;
    }

    //------------------------------------------------------------------------------------------------------------------
    // 字节数组与数字转换
    //------------------------------------------------------------------------------------------------------------------

    /**
     * byte数组转int，使用大端字节序（高位字节在前，低位字节在后）
     *
     * @param value byte数组
     * @return int
     */
    public static int intValue(byte[] value) {
        return (value[0] & 0xff) << 24 //
                | (value[1] & 0xff) << 16 //
                | (value[2] & 0xff) << 8 //
                | (value[3] & 0xff);
    }

    /**
     * 将数字转为字节数组
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return {@code byte[]}
     */
    public static byte[] toBytes(char value, boolean isLowFirst) {
        return toBytes(value, isLowFirst, char.class);
    }

    /**
     * 将数字转为字节数组
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return {@code byte[]}
     */
    public static byte[] toBytes(byte value, boolean isLowFirst) {
        return toBytes(value, isLowFirst, byte.class);
    }

    /**
     * 将数字转为字节数组
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return {@code byte[]}
     */
    public static byte[] toBytes(short value, boolean isLowFirst) {
        return toBytes(value, isLowFirst, short.class);
    }

    /**
     * 将数字转为字节数组
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return {@code byte[]}
     */
    public static byte[] toBytes(int value, boolean isLowFirst) {
        return toBytes(value, isLowFirst, int.class);
    }

    /**
     * 将数字转为字节数组
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @return {@code byte[]}
     */
    public static byte[] toBytes(long value, boolean isLowFirst) {
        return toBytes(value, isLowFirst, long.class);
    }

    /**
     * 将数字转为字节数组
     *
     * @param value      值
     * @param isLowFirst 是否低位在前
     * @param type       类型
     * @return {@code byte[]}
     */
    private static byte[] toBytes(long value, boolean isLowFirst, @Nonnull Class<?> type) {
        int byteCount = PoolOfObject.PRIMITIVE_BYTE_LENGTH.get().get(type);
        byte[] results = new byte[byteCount];
        for (int i = 0; i < byteCount; i++) {
            results[!isLowFirst ? byteCount - i - 1 : i] = (byte) (value >> (i << 3) & 0xFFL);
        }
        return results;
    }

    //------------------------------------------------------------------------------------------------------------------
    // 字节位进制位处理
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 提取位值
     *
     * @param value    值
     * @param whichBit 哪位数据
     * @return 返回那一位的真值
     */
    public static boolean extractBitVal(byte value, int whichBit) {
        if (whichBit < 1 || whichBit > PoolOfCommon.BYTE) {
            throw new IllegalArgumentException("The specified number of bits must be between 1 and 8");
        }
        return extractBit(value, whichBit);
    }

    /**
     * 提取位值
     *
     * @param value    值
     * @param whichBit 哪位数据
     * @return 返回那一位的真值
     */
    public static boolean extractBitVal(short value, int whichBit) {
        if (whichBit < 1 || whichBit > PoolOfCommon.WORD) {
            throw new IllegalArgumentException("The specified number of bits must be between 1 and 16");
        }
        return extractBit(value, whichBit);
    }

    /**
     * 提取位值
     *
     * @param value    值
     * @param whichBit 哪位数据
     * @return 返回那一位的真值
     */
    public static boolean extractBitVal(int value, int whichBit) {
        if (whichBit < 1 || whichBit > PoolOfCommon.DWORD) {
            throw new IllegalArgumentException("The specified number of bits must be between 1 and 32");
        }
        return extractBit(value, whichBit);
    }

    /**
     * 提取位值
     *
     * @param value    值
     * @param whichBit 哪位数据
     * @return 返回那一位的真值
     */
    public static boolean extractBitVal(long value, int whichBit) {
        if (whichBit < 1 || whichBit > PoolOfCommon.QWORD) {
            throw new IllegalArgumentException("The specified number of bits must be between 1 and 64");
        }
        return extractBit(value, whichBit);
    }

    /**
     * 提取位值
     *
     * @param value    值
     * @param whichBit 哪位数据
     * @return 返回那一位的真值
     */
    private static boolean extractBit(long value, int whichBit) {
        return ((value & (1L << (whichBit - 1))) >>> (whichBit - 1)) == 1;
    }

    /**
     * 提取8位值
     *
     * @param value 值
     * @return 返回8位的值
     */
    public static byte extractLowerHalfByteVal(byte value) {
        return (byte) extractBitHalfByteVal(value, 1);
    }

    /**
     * 提取8位值
     *
     * @param value 值
     * @return 返回8位的值
     */
    public static byte extractHighHalfByteVal(byte value) {
        return (byte) extractBitHalfByteVal(value, 2);
    }

    /**
     * 提取8位值
     *
     * @param value      值
     * @param whichGroup 数据哪组
     * @return 返回8位的值
     */
    public static int extractBitHalfByteVal(int value, int whichGroup) {
        return extractBitForN(value, whichGroup, 2, 8, 0xF);
    }

    /**
     * 提取4位值
     *
     * @param value      值
     * @param whichGroup 数据哪组
     * @return 返回4位的值
     */
    public static int extractBitHalfByteVal(long value, int whichGroup) {
        return extractBitForN(value, whichGroup, 2, 16, 0xF);
    }

    /**
     * 提取8位值
     *
     * @param value      值
     * @param whichGroup 数据哪组
     * @return 返回8位的值
     */
    public static int extractBitByteVal(int value, int whichGroup) {
        return extractBitForN(value, whichGroup, 3, 4, 0xFF);
    }

    /**
     * 提取8位值
     *
     * @param value      值
     * @param whichGroup 数据哪组
     * @return 返回8位的值
     */
    public static int extractBitByteVal(long value, int whichGroup) {
        return extractBitForN(value, whichGroup, 3, 8, 0xFF);
    }

    /**
     * 提取16位值
     *
     * @param value      值
     * @param whichGroup 数据哪组
     * @return 返回16位的值
     */
    public static int extractBitWordVal(int value, int whichGroup) {
        return extractBitForN(value, whichGroup, 4, 2, 0xFFFF);
    }

    /**
     * 提取16位值
     *
     * @param value      值
     * @param whichGroup 数据哪组
     * @return 返回16位的值
     */
    public static int extractBitWordVal(long value, int whichGroup) {
        return extractBitForN(value, whichGroup, 4, 4, 0xFFFF);
    }

    /**
     * 提取32位值
     *
     * @param value      值
     * @param whichGroup 数据哪组
     * @return 返回32位的值
     */
    public static int extractBitDwordVal(long value, int whichGroup) {
        return extractBitForN(value, whichGroup, 5, 2, 0xFFFFFFFF);
    }

    /**
     * 提取N位值
     *
     * @param value      值
     * @param whichGroup 数据哪组
     * @param bitPower   几次幂
     * @param totalGroup 总计多少组
     * @param mark       掩码
     * @return 返回N位的值
     */
    private static int extractBitForN(
            long value, int whichGroup, int bitPower, int totalGroup, int mark) {
        if (whichGroup < 1 || whichGroup > totalGroup) {
            throw new IllegalArgumentException("The whichGroup must be in range from 1 to " + totalGroup);
        }
        return (int) ((value >> ((whichGroup - 1) << bitPower)) & mark);
    }


    /**
     * 提取位值
     *
     * @param value    值
     * @param whichBit 哪位数据
     * @return 返回那一位的真值
     */
    public static byte putBitVal(byte data, int whichBit, boolean value) {
        if (whichBit < 1 || whichBit > PoolOfCommon.BYTE) {
            throw new IllegalArgumentException("The specified number of bits must be between 1 and 8");
        }
        return (byte) putBit(data, whichBit, value);
    }

    /**
     * 提取位值
     *
     * @param value    值
     * @param whichBit 哪位数据
     * @return 返回那一位的真值
     */
    public static short putBitVal(short data, int whichBit, boolean value) {
        if (whichBit < 1 || whichBit > PoolOfCommon.WORD) {
            throw new IllegalArgumentException("The specified number of bits must be between 1 and 16");
        }
        return (short) putBit(data, whichBit, value);
    }

    /**
     * 提取位值
     *
     * @param value    值
     * @param whichBit 哪位数据
     * @return 返回那一位的真值
     */
    public static int putBitVal(int data, int whichBit, boolean value) {
        if (whichBit < 1 || whichBit > PoolOfCommon.DWORD) {
            throw new IllegalArgumentException("The specified number of bits must be between 1 and 32");
        }
        return (int) putBit(data, whichBit, value);
    }

    /**
     * 提取位值
     *
     * @param value    值
     * @param whichBit 哪位数据
     * @return 返回那一位的真值
     */
    public static long putBitVal(long data, int whichBit, boolean value) {
        if (whichBit < 1 || whichBit > PoolOfCommon.QWORD) {
            throw new IllegalArgumentException("The specified number of bits must be between 1 and 64");
        }
        return putBit(data, whichBit, value);
    }

    /**
     * 放置位值
     *
     * @param data     数据
     * @param whichBit 哪位数据
     * @param value    值
     * @return 返回处理后的数据
     */
    private static long putBit(long data, int whichBit, boolean value) {
        return data & ((value ? 1L : 0L) << (whichBit - 1));
    }

}
