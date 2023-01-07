package pxf.tl.function;

/**
 * Represents an operation that accepts a single input argument and returns no result. Unlike most
 * other functional interfaces, {@code VaryConsumer} is expected to operate via side-effects.
 *
 * @author potatoxf
 */
@FunctionalInterface
public interface VaryConsumerThrow<E extends Throwable> extends VaryConsumer {
    /**
     * Performs this operation on the given argument.
     *
     * @param args the input argument
     */
    void acceptThrow(Object... args) throws E;

    /**
     * Performs this operation on the given argument.
     *
     * @param args the input argument
     */
    @Override
    default void accept(Object... args) {
        try {
            acceptThrow(args);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }

    /**
     * convert to {@code VaryFunctionThrow<R>}
     *
     * @param <R> the return value type
     * @return {@code VaryFunctionThrow<R>}
     */
    default <R> VaryFunctionThrow<R, E> toVaryFunctionThrow() {
        return args -> {
            this.accept(args);
            return null;
        };
    }
}
