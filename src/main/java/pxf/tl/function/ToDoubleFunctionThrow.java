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

import java.util.function.ToDoubleFunction;

/**
 * Represents a function that produces a double-valued result. This is the {@code double}-producing
 * primitive specialization for {@link FunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsDouble(Object)}.
 *
 * @param <T> the type of the input to the function
 * @see FunctionThrow
 */
@FunctionalInterface
public interface ToDoubleFunctionThrow<T, E extends Throwable> extends ToDoubleFunction<T> {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    double applyAsDoubleThrow(T value) throws E;

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    @Override
    default double applyAsDouble(T value) {
        try {
            return applyAsDoubleThrow(value);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
