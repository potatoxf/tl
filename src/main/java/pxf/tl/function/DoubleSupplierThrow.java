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

import java.util.function.DoubleSupplier;

/**
 * Represents a supplier of {@code double}-valued results. This is the {@code double}-producing
 * primitive specialization of {@link SupplierThrow}.
 *
 * <p>There is no requirement that a distinct result be returned each time the supplier is invoked.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #getAsDouble()}.
 *
 * @see SupplierThrow
 */
@FunctionalInterface
public interface DoubleSupplierThrow<E extends Throwable> extends DoubleSupplier {

    /**
     * Gets a result.
     *
     * @return a result
     */
    double getAsDoubleThrow() throws E;

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    default double getAsDouble() {
        try {
            return getAsDoubleThrow();
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
