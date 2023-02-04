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

import java.util.function.IntBinaryOperator;

/**
 * Represents an operation upon two {@code int}-valued operands and producing an {@code int}-valued
 * result. This is the primitive type specialization of {@link BinaryOperatorThrow} for {@code int}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsInt(int, int)}.
 *
 * @see BinaryOperatorThrow
 * @see IntUnaryOperatorThrow
 */
@FunctionalInterface
public interface IntBinaryOperatorThrow<E extends Throwable> extends IntBinaryOperator {

    /**
     * Applies this operator to the given operands.
     *
     * @param left  the first operand
     * @param right the second operand
     * @return the operator result
     */
    int applyAsIntThrow(int left, int right) throws E;

    /**
     * Applies this operator to the given operands.
     *
     * @param left  the first operand
     * @param right the second operand
     * @return the operator result
     */
    @Override
    default int applyAsInt(int left, int right) {
        try {
            return applyAsIntThrow(left, right);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
