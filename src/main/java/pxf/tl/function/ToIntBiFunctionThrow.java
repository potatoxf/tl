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

import java.util.function.ToIntBiFunction;

/**
 * Represents a function that accepts two arguments and produces an int-valued result. This is the
 * {@code int}-producing primitive specialization for {@link BiFunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsInt(Object, Object)}.
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @see BiFunctionThrow
 */
@FunctionalInterface
public interface ToIntBiFunctionThrow<T, U, E extends Throwable> extends ToIntBiFunction<T, U> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     */
    int applyAsIntThrow(T t, U u) throws E;

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     */
    @Override
    default int applyAsInt(T t, U u) {
        try {
            return applyAsIntThrow(t, u);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
