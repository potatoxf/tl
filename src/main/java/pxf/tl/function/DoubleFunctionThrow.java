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

import java.util.function.DoubleFunction;

/**
 * Represents a function that accepts a double-valued argument and produces a result. This is the
 * {@code double}-consuming primitive specialization for {@link FunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #apply(double)}.
 *
 * @param <R> the type of the result of the function
 * @see FunctionThrow
 */
@FunctionalInterface
public interface DoubleFunctionThrow<R, E extends Throwable> extends DoubleFunction<R> {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    R applyThrow(double value) throws E;

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    @Override
    default R apply(double value) {
        try {
            return applyThrow(value);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
