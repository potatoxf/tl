package pxf.tl.comparator;

import pxf.tl.util.ToolBytecode;

import javax.annotation.Nonnull;
import java.util.Comparator;

/**
 * 抽象比较器
 *
 * <p>实现对基本可比较对象{@code Null},{@code Comparable}进行实现, 并预留{@code doCompareBefore}和{@code
 * doCompareAfter}接口 对子类进行自定义比较
 *
 * @author potatoxf
 */
@SuppressWarnings("unchecked")
public abstract class AbstractComparator<T> implements Comparator<T> {

    /**
     * 是否空元素放在最后
     */
    protected final boolean isNullElementLast;

    public AbstractComparator() {
        this(true);
    }

    public AbstractComparator(boolean isNullElementLast) {
        this.isNullElementLast = isNullElementLast;
    }

    /**
     * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer
     * as the first argument is less than, equal to, or greater than the second.
     *
     * <p>
     *
     * <p>The implementor must ensure that {@code sgn(compare(x, y)) == -sgn(compare(y, x))} for all
     * {@code x} and {@code y}. (This implies that {@code compare(x, y)} must throw an exception if
     * and only if {@code compare(y, x)} throws an exception.)
     *
     * <p>
     *
     * <p>The implementor must also ensure that the relation is transitive: {@code ((compare(x, y)>0)
     * && (compare(y, z)>0))} implies {@code compare(x, z)>0}.
     *
     * <p>
     *
     * <p>Finally, the implementor must ensure that {@code compare(x, y)==0} implies that {@code
     * sgn(compare(x, z))==sgn(compare(y, z))} for all {@code z}.
     *
     * <p>
     *
     * <p>It is generally the case, but <i>not</i> strictly required that {@code (compare(x, y)==0) ==
     * (x.equals(y))}. Generally speaking, any comparator that violates this condition should clearly
     * indicate this fact. The recommended language is "Note: this comparator imposes orderings that
     * are inconsistent with equals."
     *
     * <p>
     *
     * <p>In the foregoing description, the notation {@code sgn(}<i>expression</i>{@code )} designates
     * the mathematical <i>signum</i> function, which is defined to return one of {@code -1}, {@code
     * 0}, or {@code 1} according to whether the value of <i>expression</i> is negative, zero, or
     * positive, respectively.
     *
     * <p>Compare two elements, if both are {@code null}, they are equal, if {@code isNullElementLast}
     * is {@code true}, the empty element is the largest, otherwise it is the smallest
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than,
     * equal to, or greater than the second.
     */
    @Override
    public final int compare(T o1, T o2) {
        Integer x = compareNull(o1, o2);
        if (x != null) {
            return x;
        }
        x = doCompareBefore(o1, o2);
        if (x != null) {
            return x;
        }

        x = compareComparable(o1, o2);

        if (x != null) {
            return x;
        }

        return doCompareAfter(o1, o2);
    }

    protected Integer compareNull(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return isNullElementLast ? 1 : -1;
        }
        if (o2 == null) {
            return isNullElementLast ? -1 : 1;
        }
        return null;
    }

    /**
     * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer
     * as the first argument is less than, equal to, or greater than the second.
     *
     * <p>
     *
     * <p>The implementor must ensure that {@code sgn(compare(x, y)) == -sgn(compare(y, x))} for all
     * {@code x} and {@code y}. (This implies that {@code compare(x, y)} must throw an exception if
     * and only if {@code compare(y, x)} throws an exception.)
     *
     * <p>
     *
     * <p>The implementor must also ensure that the relation is transitive: {@code ((compare(x, y)>0)
     * && (compare(y, z)>0))} implies {@code compare(x, z)>0}.
     *
     * <p>
     *
     * <p>Finally, the implementor must ensure that {@code compare(x, y)==0} implies that {@code
     * sgn(compare(x, z))==sgn(compare(y, z))} for all {@code z}.
     *
     * <p>
     *
     * <p>It is generally the case, but <i>not</i> strictly required that {@code (compare(x, y)==0) ==
     * (x.equals(y))}. Generally speaking, any comparator that violates this condition should clearly
     * indicate this fact. The recommended language is "Note: this comparator imposes orderings that
     * are inconsistent with equals."
     *
     * <p>
     *
     * <p>In the foregoing description, the notation {@code sgn(}<i>expression</i>{@code )} designates
     * the mathematical <i>signum</i> function, which is defined to return one of {@code -1}, {@code
     * 0}, or {@code 1} according to whether the value of <i>expression</i> is negative, zero, or
     * positive, respectively.
     *
     * <p>Compare two elements, if both are {@code null}, they are equal, if {@code isNullElementLast}
     * is {@code true}, the empty element is the largest, otherwise it is the smallest
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than,
     * equal to, or greater than the second.
     */
    protected Integer doCompareBefore(T o1, T o2) {
        return null;
    }

    /**
     * 比较可比较对象
     *
     * @param o1 {@code o1 instanceof Comparable}
     * @param o2 {@code o2 instanceof Comparable}
     * @return 如果为null则继续比较，否则返回比较值
     */
    protected Integer compareComparable(@Nonnull T o1, @Nonnull T o2) {
        if (o1 instanceof Comparable c1 && o2 instanceof Comparable c2) {
            Class<?> kc = ToolBytecode.extractGenericClass(o1.getClass(), Comparable.class, 0);
            if (kc.isAssignableFrom(o2.getClass())) {
                return c1.compareTo(c2);
            } else {
                Class<?> clz1 = o1.getClass();
                Class<?> clz2 = o2.getClass();
                if (clz1.equals(clz2)) {
                    return ((Comparable<Object>) o1).compareTo(o2);
                }
                if (clz1.isAssignableFrom(clz2)) {
                    return ((Comparable<Object>) o1).compareTo(o2);
                }
                if (clz2.isAssignableFrom(clz1)) {
                    return -((Comparable<Object>) o2).compareTo(o1);
                }
            }
        }
        return null;
    }

    /**
     * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer
     * as the first argument is less than, equal to, or greater than the second.
     *
     * <p>
     *
     * <p>The implementor must ensure that {@code sgn(compare(x, y)) == -sgn(compare(y, x))} for all
     * {@code x} and {@code y}. (This implies that {@code compare(x, y)} must throw an exception if
     * and only if {@code compare(y, x)} throws an exception.)
     *
     * <p>
     *
     * <p>The implementor must also ensure that the relation is transitive: {@code ((compare(x, y)>0)
     * && (compare(y, z)>0))} implies {@code compare(x, z)>0}.
     *
     * <p>
     *
     * <p>Finally, the implementor must ensure that {@code compare(x, y)==0} implies that {@code
     * sgn(compare(x, z))==sgn(compare(y, z))} for all {@code z}.
     *
     * <p>
     *
     * <p>It is generally the case, but <i>not</i> strictly required that {@code (compare(x, y)==0) ==
     * (x.equals(y))}. Generally speaking, any comparator that violates this condition should clearly
     * indicate this fact. The recommended language is "Note: this comparator imposes orderings that
     * are inconsistent with equals."
     *
     * <p>
     *
     * <p>In the foregoing description, the notation {@code sgn(}<i>expression</i>{@code )} designates
     * the mathematical <i>signum</i> function, which is defined to return one of {@code -1}, {@code
     * 0}, or {@code 1} according to whether the value of <i>expression</i> is negative, zero, or
     * positive, respectively.
     *
     * <p>Compare two elements, if both are {@code null}, they are equal, if {@code isNullElementLast}
     * is {@code true}, the empty element is the largest, otherwise it is the smallest
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than,
     * equal to, or greater than the second.
     */
    protected abstract int doCompareAfter(T o1, T o2);
}
