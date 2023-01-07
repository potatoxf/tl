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

import java.util.function.DoubleToLongFunction;

/**
 * Represents a function that accepts a double-valued argument and produces a long-valued result.
 * This is the {@code double}-to-{@code long} primitive specialization for {@link FunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsLong(double)}.
 *
 * @see FunctionThrow
 */
@FunctionalInterface
public interface DoubleToLongFunctionThrow<E extends Throwable> extends DoubleToLongFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    long applyAsLongThrow(double value) throws E;

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    @Override
    default long applyAsLong(double value) {
        try {
            return applyAsLongThrow(value);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
