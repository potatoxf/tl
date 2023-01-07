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

import java.util.function.ToIntFunction;

/**
 * Represents a function that produces an int-valued result. This is the {@code int}-producing
 * primitive specialization for {@link FunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsInt(Object)}.
 *
 * @param <T> the type of the input to the function
 * @see FunctionThrow
 */
@FunctionalInterface
public interface ToIntFunctionThrow<T, U, E extends Throwable> extends ToIntFunction<T> {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    int applyAsIntThrow(T value) throws E;

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    @Override
    default int applyAsInt(T value) {
        try {
            return applyAsIntThrow(value);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
