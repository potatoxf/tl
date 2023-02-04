package pxf.tl.function;

/**
 * Represents a predicate (boolean-valued function) of two arguments. This is the two-arity
 * specialization of {@link VaryPredicateThrow}.
 *
 * @author potatoxf
 */
@FunctionalInterface
public interface VaryPredicateThrow<E extends Throwable> extends VaryPredicate {
    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param args the input argument
     * @return {@code true} if the input arguments match the predicate, otherwise {@code false}
     */
    boolean testThrow(Object... args) throws E;

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param args the input argument
     * @return {@code true} if the input arguments match the predicate, otherwise {@code false}
     */
    @Override
    default boolean test(Object... args) {
        try {
            return testThrow(args);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
