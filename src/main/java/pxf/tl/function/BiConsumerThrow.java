/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package pxf.tl.function;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Represents an operation that accepts two input arguments and returns no result. This is the
 * two-arity specialization of {@link ConsumerThrow}. Unlike most other functional interfaces,
 * {@code BiConsumer} is expected to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #accept(Object, Object)}.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @see ConsumerThrow
 */
@FunctionalInterface
public interface BiConsumerThrow<T, U, E extends Throwable> extends BiConsumer<T, U> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    void acceptThrow(T t, U u) throws E;

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    @Override
    default void accept(T t, U u) {
        try {
            acceptThrow(t, u);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }

    /**
     * Returns a composed {@code BiConsumer} that performs, in sequence, this operation followed by
     * the {@code after} operation. If performing either operation throws an exception, it is relayed
     * to the caller of the composed operation. If performing this operation throws an exception, the
     * {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code BiConsumer} that performs in sequence this operation followed by the
     * {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default BiConsumerThrow<T, U, E> andThen(
            BiConsumerThrow<? super T, ? super U, ? extends E> after) {
        Objects.requireNonNull(after);
        return (l, r) -> {
            acceptThrow(l, r);
            after.acceptThrow(l, r);
        };
    }
}
