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

import java.util.function.LongBinaryOperator;

/**
 * Represents an operation upon two {@code long}-valued operands and producing a {@code long}-valued
 * result. This is the primitive type specialization of {@link BinaryOperatorThrow} for {@code
 * long}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsLong(long, long)}.
 *
 * @see BinaryOperatorThrow
 * @see LongUnaryOperatorThrow
 */
@FunctionalInterface
public interface LongBinaryOperatorThrow<E extends Throwable> extends LongBinaryOperator {

    /**
     * Applies this operator to the given operands.
     *
     * @param left  the first operand
     * @param right the second operand
     * @return the operator result
     */
    long applyAsLongThrow(long left, long right) throws E;

    /**
     * Applies this operator to the given operands.
     *
     * @param left  the first operand
     * @param right the second operand
     * @return the operator result
     */
    @Override
    default long applyAsLong(long left, long right) {
        try {
            return applyAsLongThrow(left, right);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
