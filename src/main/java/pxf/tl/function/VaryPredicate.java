package pxf.tl.function;

/**
 * Represents a predicate (boolean-valued function) of two arguments. This is the two-arity
 * specialization of {@link VaryPredicate}.
 *
 * @author potatoxf
 */
@FunctionalInterface
public interface VaryPredicate {
    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param args the input argument
     * @return {@code true} if the input arguments match the predicate, otherwise {@code false}
     */
    boolean test(Object... args);
}
