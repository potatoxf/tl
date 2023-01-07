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

import java.util.function.DoubleToIntFunction;

/**
 * Represents a function that accepts a double-valued argument and produces an int-valued result.
 * This is the {@code double}-to-{@code int} primitive specialization for {@link FunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsInt(double)}.
 *
 * @see FunctionThrow
 */
@FunctionalInterface
public interface DoubleToIntFunctionThrow<E extends Throwable> extends DoubleToIntFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    int applyAsIntThrow(double value) throws E;

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    @Override
    default int applyAsInt(double value) {
        try {
            return applyAsIntThrow(value);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
