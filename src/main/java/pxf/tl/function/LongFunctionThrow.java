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

import java.util.function.LongFunction;

/**
 * Represents a function that accepts a long-valued argument and produces a result. This is the
 * {@code long}-consuming primitive specialization for {@link FunctionThrow}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #apply(long)}.
 *
 * @param <R> the type of the result of the function
 * @see FunctionThrow
 */
@FunctionalInterface
public interface LongFunctionThrow<R, E extends Throwable> extends LongFunction<R> {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    R apply(long value);
}
