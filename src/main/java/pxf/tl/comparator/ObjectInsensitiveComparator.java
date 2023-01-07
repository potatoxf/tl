package pxf.tl.comparator;

/**
 * @author potatoxf
 */
public class ObjectInsensitiveComparator<T> extends AbstractComparator<T> {

    @Override
    protected Integer doCompareBefore(T o1, T o2) {
        if (o1 instanceof String s1 && o2 instanceof String s2) {
            return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
        }
        return super.doCompareBefore(o1, o2);
    }

    @Override
    protected int doCompareAfter(T o1, T o2) {
        return Integer.compare(o1.hashCode(), o2.hashCode());
    }
}
