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

import java.util.function.ToLongFunction;

/**
 * Represents a function that produces a long-valued result. This is the {@code long}-producing
 * primitive specialization for {@link FunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsLong(Object)}.
 *
 * @param <T> the type of the input to the function
 * @see FunctionThrow
 */
@FunctionalInterface
public interface ToLongFunctionThrow<T, E extends Throwable> extends ToLongFunction<T> {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    long applyAsLongThrow(T value) throws E;

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    @Override
    default long applyAsLong(T value) {
        try {
            return applyAsLongThrow(value);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
