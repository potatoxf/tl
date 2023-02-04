package pxf.tl.api;


import pxf.tl.iter.NumberIterator;

import java.io.Serializable;
import java.util.Iterator;

/**
 * 范围
 *
 * <p>描述了范围，它的最小值为0，最大值为{@code Integer.MAX_VALUE}。
 *
 * <p>这个范围只是描述了上下限，但这里的上下限是否可以取值由使用者来保证。
 *
 * @author potatoxf
 */
public class IntScope implements Comparable<IntScope>, Iterable<Integer>, Cloneable, Serializable {

    private static final int MIN = Integer.MIN_VALUE;
    private static final int MAX = Integer.MAX_VALUE;
    private final int lower;
    private final int upper;

    protected IntScope(Integer min, Integer max) {
        int lower = min == null ? MIN : min;
        int upper = max == null ? MAX : max;
        if (lower > upper) {
            throw new IllegalArgumentException("The max must greater than min");
        }
        this.lower = lower;
        this.upper = upper;
    }

    /**
     * 构造大于某个值的范围
     *
     * <p>最大值是无限的，但是由于计算机限制最大值是{@code StringRange.MAX}
     *
     * @param value 最大的值
     * @return {@code StringRange}
     * @throws IllegalArgumentException 如果lo小于{@code StringRange.MIN}
     */
    public static IntScope gt(int value) {
        return new IntScope(value, null);
    }

    /**
     * 构造小于某个值的范围
     *
     * <p>最小值是{@code StringRange.MIN}
     *
     * @param value 最大的值
     * @return {@code StringRange}
     * @throws IllegalArgumentException 如果lo小于{@code StringRange.MIN}
     */
    public static IntScope lt(int value) {
        return new IntScope(null, value);
    }

    /**
     * 构造上下限范围
     *
     * @param lo 下限
     * @param hi 上限
     * @return {@code StringRange}
     * @throws IllegalArgumentException 如果lo小于{@code StringRange.MIN}或者lo大于hi
     */
    public static IntScope of(int lo, int hi) {
        return new IntScope(lo, hi);
    }

    /**
     * 是否在范围内
     *
     * @param value 判断的值
     * @return true在范围内，否则false
     */
    public boolean isInRange(int value) {
        return value >= lower && value < upper;
    }

    /**
     * 求返回交集
     *
     * @param other 其它范围
     * @return {@code Range}
     */
    public IntScope and(IntScope other) {
        if (other.lower >= upper) {
            return new IntScope(-1, -1);
        }
        return new IntScope(Math.max(lower, other.lower), Math.min(upper, other.upper));
    }

    /**
     * 求返回并集
     *
     * @param other 其它范围
     * @return {@code Range}
     */
    public IntScope or(IntScope other) {
        return new IntScope(Math.min(lower, other.lower), Math.max(upper, other.upper));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntScope integers = (IntScope) o;

        if (lower != integers.lower) return false;
        return upper == integers.upper;
    }

    @Override
    public int hashCode() {
        int result = lower;
        result = 31 * result + upper;
        return result;
    }

    @Override
    public int compareTo(final IntScope o) {
        // 这个范围完全在另一个范围前面
        if (upper <= o.lower) {
            return -1;
        }
        // 这个范围完全在另一个范围后面
        if (lower >= o.upper) {
            return 1;
        }
        // 相等
        if (lower == o.lower && upper == o.upper) {
            return 0;
        }
        int thisRange = upper - lower;
        int otherRange = o.upper - o.lower;
        int u, ou;
        // 确定小范围上限
        if (thisRange < otherRange) {
            u = upper;
            ou = o.upper;
        } else {
            u = o.upper;
            ou = upper;
        }
        // 当一个范围小的上限大于另一个范围大的上限时则这个小范围优先级靠后
        return u >= ou ? 1 : -1;
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Integer> iterator() {
        return new NumberIterator(lower, upper);
    }

    @Override
    public IntScope clone() {
        try {
            return (IntScope) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
