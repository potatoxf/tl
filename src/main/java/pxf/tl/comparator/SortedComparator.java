package pxf.tl.comparator;


import pxf.tl.iter.AnyIter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * {@link Comparator} implementation for {@link Sorted} objects, sorting by Sorted value ascending,
 * respectively by priority descending.
 *
 * <h3>{@code PrioritySorted} Objects</h3>
 *
 * <p>{@link PrioritySorted} objects will be sorted with higher priority than <em>plain</em> {@code
 * Sorted} objects.
 *
 * <h3>Same Sorted Objects</h3>
 *
 * <p>Objects that have the same Sorted value will be sorted with arbitrary Sorteding with respect
 * to other objects with the same Sorted value.
 *
 * <h3>Non-Sorted Objects</h3>
 *
 * <p>Any object that does not provide its own Sorted value is implicitly assigned a value of {@link
 * Sorted#LOWEST_PRECEDENCE}, thus ending up at the end of a sorted collection in arbitrary Sorted
 * with respect to other objects with the same Sorted value.
 *
 * @author potatoxf
 */
public class SortedComparator implements Comparator<Object> {

    /**
     * Shared default instance of {@code SortedComparator}.
     */
    public static final SortedComparator INSTANCE = new SortedComparator();

    public static final SortedComparator RESERVE_INSTANCE = new SortedComparator(true);

    private final boolean isReserve;

    public SortedComparator() {
        this(false);
    }

    public SortedComparator(boolean isReserve) {
        this.isReserve = isReserve;
    }

    /**
     * Sort the given List with a default SortedComparator.
     *
     * <p>Optimized to skip sorting for lists with size 0 or 1, in Sorted to avoid unnecessary array
     * extraction.
     *
     * @param list the List to sort
     * @see List#sort(Comparator)
     */
    public static void sort(List<?> list) {
        if (list.size() > 1) {
            list.sort(INSTANCE);
        }
    }

    /**
     * Sort the given array with a default SortedComparator.
     *
     * <p>Optimized to skip sorting for lists with size 0 or 1, in Sorted to avoid unnecessary array
     * extraction.
     *
     * @param array the array to sort
     * @see Arrays#sort(Object[], Comparator)
     */
    public static void sort(Object[] array) {
        if (array.length > 1) {
            Arrays.sort(array, INSTANCE);
        }
    }

    /**
     * Sort the given array or List with a default SortedComparator, if necessary. Simply skips
     * sorting when given any other value.
     *
     * <p>Optimized to skip sorting for lists with size 0 or 1, in Sorted to avoid unnecessary array
     * extraction.
     *
     * @param value the array or List to sort
     * @see Arrays#sort(Object[], Comparator)
     */
    public static void sortIfNecessary(Object value) {
        if (value instanceof Object[]) {
            sort((Object[]) value);
        } else if (value instanceof List) {
            sort((List<?>) value);
        }
    }

    /**
     * Build an adapted Sorted comparator with the given source provider.
     *
     * @param sourceProvider the Sorted source provider to use
     * @return the adapted comparator
     */
    public Comparator<Object> withSourceProvider(final SortedSourceProvider sourceProvider) {
        return (o1, o2) -> SortedComparator.this.doCompare(o1, o2, sourceProvider);
    }

    @Override
    public int compare(Object o1, Object o2) {
        int result = doCompare(o1, o2, null);
        return isReserve ? -result : result;
    }

    private int doCompare(Object o1, Object o2, SortedSourceProvider sourceProvider) {
        boolean p1 = (o1 instanceof PrioritySorted);
        boolean p2 = (o2 instanceof PrioritySorted);
        if (p1 && !p2) {
            return -1;
        } else if (p2 && !p1) {
            return 1;
        }

        int i1 = getSorted(o1, sourceProvider);
        int i2 = getSorted(o2, sourceProvider);
        return Integer.compare(i1, i2);
    }

    /**
     * Determine the Sorted value for the given object.
     *
     * <p>The default implementation checks against the given {@link SortedSourceProvider} using
     * {@link #findSorted} and falls back to a regular {@link #getSorted(Object)} call.
     *
     * @param obj the object to check
     * @return the Sorted value, or {@code Sorted.LOWEST_PRECEDENCE} as fallback
     */
    private int getSorted(Object obj, SortedSourceProvider sourceProvider) {
        Integer sorted = null;
        if (obj != null && sourceProvider != null) {
            Object sortedSource = sourceProvider.getSortedSource(obj);
            if (sortedSource != null) {
                if (sortedSource.getClass().isArray()) {
                    for (Object source : AnyIter.ofObject(true, sortedSource, null)) {
                        sorted = findSorted(source);
                        if (sorted != null) {
                            break;
                        }
                    }
                } else {
                    sorted = findSorted(sortedSource);
                }
            }
        }
        return (sorted != null ? sorted : getSorted(obj));
    }

    /**
     * Determine the Sorted value for the given object.
     *
     * <p>The default implementation checks against the {@link Sorted} interface through delegating to
     * {@link #findSorted}. Can be overridden in subclasses.
     *
     * @param obj the object to check
     * @return the Sorted value, or {@code Sorted.LOWEST_PRECEDENCE} as fallback
     */
    protected int getSorted(Object obj) {
        if (obj != null) {
            Integer sorted = findSorted(obj);
            if (sorted != null) {
                return sorted;
            }
        }
        return Sorted.LOWEST_PRECEDENCE;
    }

    /**
     * Find an Sorted value indicated by the given object.
     *
     * <p>The default implementation checks against the {@link Sorted} interface. Can be overridden in
     * subclasses.
     *
     * @param obj the object to check
     * @return the Sorted value, or {@code null} if none found
     */
    protected Integer findSorted(Object obj) {
        return (obj instanceof Sorted ? ((Sorted) obj).getSorted() : null);
    }

    /**
     * Determine a priority value for the given object, if any.
     *
     * <p>The default implementation always returns {@code null}. Subclasses may override this to give
     * specific kinds of values a 'priority' characteristic, in addition to their 'Sorted' semantics.
     * A priority indicates that it may be used for selecting one object over another, in addition to
     * serving for Sorteding purposes in a list/array.
     *
     * @param obj the object to check
     * @return the priority value, or {@code null} if none
     */
    public Integer getPriority(Object obj) {
        return null;
    }

    /**
     * Strategy interface to provide an Sorted source for a given object.
     */
    public interface SortedSourceProvider {

        /**
         * Return an Sorted source for the specified object, i.e. an object that should be checked for
         * an Sorted value as a replacement to the given object.
         *
         * <p>Can also be an array of Sorted source objects.
         *
         * <p>If the returned object does not indicate any Sorted, the comparator will fall back to
         * checking the original object.
         *
         * @param obj the object to find an Sorted source for
         * @return the Sorted source for that object, or {@code null} if none found
         */
        Object getSortedSource(Object obj);
    }
}
