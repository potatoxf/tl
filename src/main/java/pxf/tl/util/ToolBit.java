package pxf.tl.util;

import java.math.BigInteger;

/**
 * @author potatoxf
 */
public final class ToolBit {
    /**
     * 将指定位置清除为设置为1
     *
     * @param target 目标位
     * @return 返回处理后的结果
     */
    public static String setBit(int... target) {
        return setBit(BigInteger.ZERO, target);
    }

    /**
     * 将指定位置清除为设置为1
     *
     * @param input  输入值
     * @param target 目标位
     * @return 返回处理后的结果
     */
    public static String setBit(String input, int... target) {
        return setBit(new BigInteger(input), target);
    }

    /**
     * 将指定位置清除为设置为1
     *
     * @param input  输入值
     * @param target 目标位
     * @return 返回处理后的结果
     */
    public static String setBit(long input, int... target) {
        return setBit(BigInteger.valueOf(input), target);
    }

    /**
     * 将指定位置清除为设置为1
     *
     * @param input  输入值
     * @param target 目标位
     * @return 返回处理后的结果
     */
    public static String setBit(BigInteger input, int... target) {
        BigInteger value = input == null ? BigInteger.ZERO : input;
        for (int i : target) {
            if (i <= 0) {
                throw new IllegalArgumentException("The specified bit must be greater than 0");
            }
            value = value.setBit(i);
        }
        return value.toString();
    }

    /**
     * 将指定位置清除为0
     *
     * @param target 目标位
     * @return 返回处理后的结果
     */
    public static String clearBit(int... target) {
        return clearBit(BigInteger.ZERO, target);
    }

    /**
     * 将指定位置清除为0
     *
     * @param input  输入值
     * @param target 目标位
     * @return 返回处理后的结果
     */
    public static String clearBit(String input, int... target) {
        return clearBit(new BigInteger(input), target);
    }

    /**
     * 将指定位置清除为0
     *
     * @param input  输入值
     * @param target 目标位
     * @return 返回处理后的结果
     */
    public static String clearBit(long input, int... target) {
        return clearBit(BigInteger.valueOf(input), target);
    }

    /**
     * 将指定位置清除为0
     *
     * @param input  输入值
     * @param target 目标位
     * @return 返回处理后的结果
     */
    public static String clearBit(BigInteger input, int... target) {
        BigInteger value = input == null ? BigInteger.ZERO : input;
        for (int i : target) {
            if (i <= 0) {
                throw new IllegalArgumentException("The specified bit must be greater than 0");
            }
            value = value.clearBit(i);
        }
        return value.toString();
    }

    /**
     * 将指定位置取反
     *
     * @param target 目标位
     * @return 返回处理后的结果
     */
    public static String flipBit(int... target) {
        return flipBit(BigInteger.ZERO, target);
    }

    /**
     * 将指定位置取反
     *
     * @param input  输入值
     * @param target 目标位
     * @return 返回处理后的结果
     */
    public static String flipBit(String input, int... target) {
        return flipBit(new BigInteger(input), target);
    }

    /**
     * 将指定位置取反
     *
     * @param input  输入值
     * @param target 目标位
     * @return 返回处理后的结果
     */
    public static String flipBit(long input, int... target) {
        return flipBit(BigInteger.valueOf(input), target);
    }

    /**
     * 将指定位置取反
     *
     * @param input  输入值
     * @param target 目标位
     * @return 返回处理后的结果
     */
    public static String flipBit(BigInteger input, int... target) {
        BigInteger value = input == null ? BigInteger.ZERO : input;
        for (int i : target) {
            if (i <= 0) {
                throw new IllegalArgumentException("The specified bit must be greater than 0");
            }
            value = value.flipBit(i);
        }
        return value.toString();
    }

    /**
     * 测试指定位上是否至少一个为1
     *
     * @param input  输入值
     * @param target 目标位
     * @return 指定位上为1返回true，否则返回false
     */
    public static boolean testAnyBit(String input, int... target) {
        return testBit(input, 1, target);
    }

    /**
     * 测试指定位上是否至少一个为1
     *
     * @param input  输入值
     * @param target 目标位
     * @return 指定位上为1返回true，否则返回false
     */
    public static boolean testAnyBit(long input, int... target) {
        return testBit(input, 1, target);
    }

    /**
     * 测试指定位上是否至少一个为1
     *
     * @param input  输入值
     * @param target 目标位
     * @return 指定位上为1返回true，否则返回false
     */
    public static boolean testAnyBit(BigInteger input, int... target) {
        return testBit(input, 1, target);
    }

    /**
     * 测试指定位上是否全部为1
     *
     * @param input  输入值
     * @param target 目标位
     * @return 指定位上为1返回true，否则返回false
     */
    public static boolean testAllBit(String input, int... target) {
        return testBit(input, target.length, target);
    }

    /**
     * 测试指定位上是否全部为1
     *
     * @param input  输入值
     * @param target 目标位
     * @return 指定位上为1返回true，否则返回false
     */
    public static boolean testAllBit(long input, int... target) {
        return testBit(input, target.length, target);
    }

    /**
     * 测试指定位上是否全部为1
     *
     * @param input  输入值
     * @param target 目标位
     * @return 指定位上为1返回true，否则返回false
     */
    public static boolean testAllBit(BigInteger input, int... target) {
        return testBit(input, target.length, target);
    }

    /**
     * 测试指定位上是否为1
     *
     * @param input  输入值
     * @param count  至少true的个数
     * @param target 目标位
     * @return 指定位上为1返回true，否则返回false
     */
    public static boolean testBit(String input, int count, int... target) {
        return testBit(new BigInteger(input), count, target);
    }

    /**
     * 测试指定位上是否为1
     *
     * @param input  输入值
     * @param count  至少true的个数
     * @param target 目标位
     * @return 指定位上为1返回true，否则返回false
     */
    public static boolean testBit(long input, int count, int... target) {
        BigInteger value = BigInteger.valueOf(input);
        return testBit(value, count, target);
    }

    /**
     * 测试指定位上是否为1
     *
     * @param input  输入值
     * @param count  至少true的个数
     * @param target 目标位
     * @return 指定位上为1返回true，否则返回false
     */
    public static boolean testBit(BigInteger input, int count, int... target) {
        count = Math.min(count, target.length);
        for (int i : target) {
            if (i <= 0) {
                throw new IllegalArgumentException("The specified bit must be greater than 0");
            }
            if (input.testBit(i)) {
                count--;
            }
            if (count == 0) {
                return true;
            }
        }
        return false;
    }
}
