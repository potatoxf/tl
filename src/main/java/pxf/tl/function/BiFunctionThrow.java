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
import java.util.function.BiFunction;

/**
 * Represents a function that accepts two arguments and produces a result. This is the two-arity
 * specialization of {@link FunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #apply(Object, Object)}.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <R> the type of the result of the function
 * @see FunctionThrow
 */
@FunctionalInterface
public interface BiFunctionThrow<T, U, R, E extends Throwable> extends BiFunction<T, U, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     */
    R applyThrow(T t, U u) throws E;

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     */
    @Override
    default R apply(T t, U u) {
        try {
            return applyThrow(t, u);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }

    /**
     * Returns a composed function that first applies this function to its input, and then applies the
     * {@code after} function to the result. If evaluation of either function throws an exception, it
     * is relayed to the caller of the composed function.
     *
     * @param <V>   the type of output of the {@code after} function, and of the composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then applies the {@code after}
     * function
     * @throws NullPointerException if after is null
     */
    default <V> BiFunctionThrow<T, U, V, E> andThen(
            FunctionThrow<? super R, ? extends V, ? extends E> after) {
        Objects.requireNonNull(after);
        return (T t, U u) -> after.apply(apply(t, u));
    }
}
