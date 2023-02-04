package pxf.tl.function;

/**
 * Represents a function that accepts two arguments and produces a result. This is the two-arity
 * specialization of {@link VaryFunctionThrow}.
 *
 * @author potatoxf
 */
@FunctionalInterface
public interface VaryFunctionThrow<R, E extends Throwable> extends VaryFunction<R> {
    /**
     * Applies this function to the given arguments.
     *
     * @param args the function argument
     * @return the function result
     */
    R applyThrow(Object... args) throws E;

    /**
     * Applies this function to the given arguments.
     *
     * @param args the function argument
     * @return the function result
     */
    @Override
    default R apply(Object... args) {
        try {
            return applyThrow(args);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }

    /**
     * convert to {@code VaryConsumerThrow<E>}
     *
     * @return {@code VaryConsumerThrow<E>}
     */
    default VaryConsumerThrow<E> toVaryConsumerThrow() {
        return this::apply;
    }
}
