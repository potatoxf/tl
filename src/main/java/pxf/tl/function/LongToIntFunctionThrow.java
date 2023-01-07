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

import java.util.function.LongToIntFunction;

/**
 * Represents a function that accepts a long-valued argument and produces an int-valued result. This
 * is the {@code long}-to-{@code int} primitive specialization for {@link FunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsInt(long)}.
 *
 * @see FunctionThrow
 */
@FunctionalInterface
public interface LongToIntFunctionThrow<E extends Throwable> extends LongToIntFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    int applyAsIntThrow(long value) throws E;

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    @Override
    default int applyAsInt(long value) {
        try {
            return applyAsIntThrow(value);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
