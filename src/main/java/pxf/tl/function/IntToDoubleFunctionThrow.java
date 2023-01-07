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

import java.util.function.IntToDoubleFunction;

/**
 * Represents a function that accepts an int-valued argument and produces a double-valued result.
 * This is the {@code int}-to-{@code double} primitive specialization for {@link FunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsDouble(int)}.
 *
 * @see FunctionThrow
 */
@FunctionalInterface
public interface IntToDoubleFunctionThrow<E extends Throwable> extends IntToDoubleFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    double applyAsDoubleThrow(int value) throws E;

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    @Override
    default double applyAsDouble(int value) {
        try {
            return applyAsDoubleThrow(value);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
