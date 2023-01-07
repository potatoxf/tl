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

import java.util.function.IntToLongFunction;

/**
 * Represents a function that accepts an int-valued argument and produces a long-valued result. This
 * is the {@code int}-to-{@code long} primitive specialization for {@link FunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsLong(int)}.
 *
 * @see FunctionThrow
 */
@FunctionalInterface
public interface IntToLongFunctionThrow<E extends Throwable> extends IntToLongFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    long applyAsLongThrow(int value) throws E;

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    @Override
    default long applyAsLong(int value) {
        try {
            return applyAsLongThrow(value);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
