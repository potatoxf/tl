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
import java.util.function.Function;

/**
 * Represents a function that accepts one argument and produces a result.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #apply(Object)}.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface FunctionThrow<T, R, E extends Throwable> extends Function<T, R> {

    /**
     * Returns a function that always returns its input argument.
     *
     * @param <T> the type of the input and output objects to the function
     * @return a function that always returns its input argument
     */
    static <T, E extends Throwable> FunctionThrow<T, T, E> identity() {
        return t -> t;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R applyThrow(T t) throws E;

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    @Override
    default R apply(T t) {
        try {
            return applyThrow(t);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }

    /**
     * Returns a composed function that first applies the {@code before} function to its input, and
     * then applies this function to the result. If evaluation of either function throws an exception,
     * it is relayed to the caller of the composed function.
     *
     * @param <V>    the type of input to the {@code before} function, and to the composed function
     * @param before the function to apply before this function is applied
     * @return a composed function that first applies the {@code before} function and then applies
     * this function
     * @throws NullPointerException if before is null
     * @see #andThen(FunctionThrow)
     */
    default <V> FunctionThrow<V, R, E> compose(
            FunctionThrow<? super V, ? extends T, ? extends E> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
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
     * @see #compose(FunctionThrow)
     */
    default <V> FunctionThrow<T, V, E> andThen(
            FunctionThrow<? super R, ? extends V, ? extends E> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }
}
