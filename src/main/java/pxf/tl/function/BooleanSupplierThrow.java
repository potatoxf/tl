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

import java.util.function.BooleanSupplier;

/**
 * Represents a supplier of {@code boolean}-valued results. This is the {@code boolean}-producing
 * primitive specialization of {@link SupplierThrow}.
 *
 * <p>There is no requirement that a new or distinct result be returned each time the supplier is
 * invoked.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #getAsBoolean()}.
 *
 * @see SupplierThrow
 */
@FunctionalInterface
public interface BooleanSupplierThrow<E extends Throwable> extends BooleanSupplier {

    /**
     * Gets a result.
     *
     * @return a result
     */
    boolean getAsBooleanThrow() throws E;

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    default boolean getAsBoolean() {
        try {
            return getAsBooleanThrow();
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
