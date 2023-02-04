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

import java.util.function.LongToDoubleFunction;

/**
 * Represents a function that accepts a long-valued argument and produces a double-valued result.
 * This is the {@code long}-to-{@code double} primitive specialization for {@link FunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsDouble(long)}.
 *
 * @see FunctionThrow
 */
@FunctionalInterface
public interface LongToDoubleFunctionThrow<E extends Throwable> extends LongToDoubleFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    double applyAsDoubleThrow(long value) throws E;

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    @Override
    default double applyAsDouble(long value) {
        try {
            return applyAsDoubleThrow(value);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
