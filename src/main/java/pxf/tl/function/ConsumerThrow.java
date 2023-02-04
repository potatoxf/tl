/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
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
import java.util.function.Consumer;

/**
 * Represents an operation that accepts a single input argument and returns no result. Unlike most
 * other functional interfaces, {@code Consumer} is expected to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #accept(Object)}.
 *
 * @param <T> the type of the input to the operation
 */
@FunctionalInterface
public interface ConsumerThrow<T, E extends Throwable> extends Consumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void acceptThrow(T t) throws E;

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    @Override
    default void accept(T t) {
        try {
            acceptThrow(t);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }

    /**
     * Returns a composed {@code Consumer} that performs, in sequence, this operation followed by the
     * {@code after} operation. If performing either operation throws an exception, it is relayed to
     * the caller of the composed operation. If performing this operation throws an exception, the
     * {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code Consumer} that performs in sequence this operation followed by the
     * {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default ConsumerThrow<T, E> andThen(ConsumerThrow<? super T, ? extends E> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            acceptThrow(t);
            after.acceptThrow(t);
        };
    }
}
