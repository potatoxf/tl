package pxf.tl.function;

/**
 * Represents a function that accepts two arguments and produces a result. This is the two-arity
 * specialization of {@link VaryFunction}.
 *
 * @author potatoxf
 */
@FunctionalInterface
public interface VaryFunction<R> {
    /**
     * Applies this function to the given arguments.
     *
     * @param args the function argument
     * @return the function result
     */
    R apply(Object... args);

    /**
     * convert to {@code VaryConsumer}
     *
     * @return {@code VaryConsumer}
     */
    default VaryConsumer toVaryConsumer() {
        return this::apply;
    }
}
