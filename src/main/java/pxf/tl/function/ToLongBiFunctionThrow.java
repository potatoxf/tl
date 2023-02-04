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

import java.util.function.ToLongBiFunction;

/**
 * Represents a function that accepts two arguments and produces a long-valued result. This is the
 * {@code long}-producing primitive specialization for {@link BiFunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsLong(Object, Object)}.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @see BiFunctionThrow
 */
@FunctionalInterface
public interface ToLongBiFunctionThrow<T, U, E extends Throwable> extends ToLongBiFunction<T, U> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     */
    long applyAsLongThrow(T t, U u) throws E;

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     */
    @Override
    default long applyAsLong(T t, U u) {
        try {
            return applyAsLongThrow(t, u);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
