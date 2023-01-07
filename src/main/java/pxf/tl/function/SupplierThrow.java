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

import java.util.function.Supplier;

/**
 * Represents a supplier of results.
 *
 * <p>There is no requirement that a new or distinct result be returned each time the supplier is
 * invoked.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #get()}.
 *
 * @param <T> the type of results supplied by this supplier
 */
@FunctionalInterface
public interface SupplierThrow<T, E extends Throwable> extends Supplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T getThrow() throws E;

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    default T get() {
        try {
            return getThrow();
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
