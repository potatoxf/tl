package pxf.tl.comparator;


import pxf.tl.function.FunctionThrow;

/**
 * 内部元素元素比较器，用于比较对象内部元素
 *
 * <p>经常用于对{@code Map}对象进行比较，选取内部元素进行比较
 *
 * @author potatoxf
 */
public class InternalComparator<T, E> extends AbstractComparator<T> {
    private final FunctionThrow<T, E, RuntimeException> factoryThrow;

    private final AbstractComparator<E> elementComparator;

    public InternalComparator(FunctionThrow<T, E, RuntimeException> factoryThrow) {
        this(true, factoryThrow);
    }

    public InternalComparator(
            boolean isNullElementLast, FunctionThrow<T, E, RuntimeException> factoryThrow) {
        super(isNullElementLast);
        this.factoryThrow = factoryThrow;
        this.elementComparator =
                new AbstractComparator<E>(isNullElementLast) {
                    @Override
                    protected int doCompareAfter(E o1, E o2) {
                        return o1.toString().compareTo(o2.toString());
                    }
                };
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
    protected int doCompareAfter(T o1, T o2) {
        return elementComparator.compare(factoryThrow.apply(o1), factoryThrow.apply(o2));
    }
}
