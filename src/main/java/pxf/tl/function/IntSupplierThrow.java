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

import java.util.function.IntSupplier;

/**
 * Represents a supplier of {@code int}-valued results. This is the {@code int}-producing primitive
 * specialization of {@link SupplierThrow}.
 *
 * <p>There is no requirement that a distinct result be returned each time the supplier is invoked.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #getAsInt()}.
 *
 * @see SupplierThrow
 */
@FunctionalInterface
public interface IntSupplierThrow<E extends Throwable> extends IntSupplier {

    /**
     * Gets a result.
     *
     * @return a result
     */
    int getAsIntThrow() throws E;

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    default int getAsInt() {
        try {
            return getAsIntThrow();
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
