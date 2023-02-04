/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.util.function.ObjDoubleConsumer;

/**
 * Represents an operation that accepts an object-valued and a {@code double}-valued argument, and
 * returns no result. This is the {@code (reference, double)} specialization of {@link
 * BiConsumerThrow}. Unlike most other functional interfaces, {@code ObjDoubleConsumer} is expected
 * to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #accept(Object, double)}.
 *
 * @param <T> the type of the object argument to the operation
 * @see BiConsumerThrow
 */
@FunctionalInterface
public interface ObjDoubleConsumerThrow<T, E extends Throwable> extends ObjDoubleConsumer<T> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t     the first input argument
     * @param value the second input argument
     */
    void acceptThrow(T t, double value) throws E;

    /**
     * Performs this operation on the given arguments.
     *
     * @param t     the first input argument
     * @param value the second input argument
     */
    @Override
    default void accept(T t, double value) {
        try {
            acceptThrow(t, value);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
