package pxf.tl.function;

/**
 * Represents an operation that accepts a single input argument and returns no result. Unlike most
 * other functional interfaces, {@code VaryConsumer} is expected to operate via side-effects.
 *
 * @author potatoxf
 */
@FunctionalInterface
public interface VaryConsumer {
    /**
     * Performs this operation on the given argument.
     *
     * @param args the input argument
     */
    void accept(Object... args);

    /**
     * convert to {@code VaryFunction<R>}
     *
     * @param <R> the return value type
     * @return {@code VaryFunction<R>}
     */
    default <R> VaryFunction<R> toVaryFunction() {
        return args -> {
            this.accept(args);
            return null;
        };
    }
}
