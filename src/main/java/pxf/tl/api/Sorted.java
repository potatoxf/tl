package pxf.tl.api;


import pxf.tl.comparator.SortedComparator;

/**
 * {@code Sorted} is an interface that can be implemented by objects that should be
 * <em>orderable</em>, for example in a {@code Collection}.
 *
 * <p>The actual {@link #getSorted() order} can be interpreted as prioritization, with the first
 * object (with the lowest order value) having the highest priority.
 *
 * <p>Note that there is also a <em>priority</em> marker for this interface: {@link PrioritySorted}.
 * Consult the Javadoc for {@code PrioritySorted} for details on how {@code PrioritySorted} objects
 * are ordered relative to <em>plain</em> {@link Sorted} objects.
 *
 * <p>Consult the Javadoc for {@link SortedComparator} for details on the sort semantics for
 * non-ordered objects.
 *
 * @author potatoxf
 */
public interface Sorted {

    /**
     * Useful constant for the highest precedence value.
     *
     * @see Integer#MIN_VALUE
     */
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    /**
     * Useful constant for the lowest precedence value.
     *
     * @see Integer#MAX_VALUE
     */
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    /**
     * Get the order value of this object.
     *
     * <p>Higher values are interpreted as lower priority. As a consequence, the object with the
     * lowest value has the highest priority (somewhat analogous to Servlet {@code load-on-startup}
     * values).
     *
     * <p>Same order values will result in arbitrary sort positions for the affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    default int getSorted() {
        return LOWEST_PRECEDENCE;
    }
}
