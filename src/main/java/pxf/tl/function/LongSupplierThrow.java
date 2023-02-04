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

import java.util.function.LongSupplier;

/**
 * Represents a supplier of {@code long}-valued results. This is the {@code long}-producing
 * primitive specialization of {@link SupplierThrow}.
 *
 * <p>There is no requirement that a distinct result be returned each time the supplier is invoked.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #getAsLong()}.
 *
 * @see SupplierThrow
 */
@FunctionalInterface
public interface LongSupplierThrow<E extends Throwable> extends LongSupplier {

    /**
     * Gets a result.
     *
     * @return a result
     */
    long getAsLongThrow() throws E;

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    default long getAsLong() {
        try {
            return getAsLongThrow();
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
