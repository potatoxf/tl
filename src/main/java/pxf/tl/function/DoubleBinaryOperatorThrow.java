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

import java.util.function.DoubleBinaryOperator;

/**
 * Represents an operation upon two {@code double}-valued operands and producing a {@code
 * double}-valued result. This is the primitive type specialization of {@link BinaryOperatorThrow}
 * for {@code double}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsDouble(double, double)}.
 *
 * @see BinaryOperatorThrow
 * @see DoubleUnaryOperatorThrow
 */
@FunctionalInterface
public interface DoubleBinaryOperatorThrow<E extends Throwable> extends DoubleBinaryOperator {
    /**
     * Applies this operator to the given operands.
     *
     * @param left  the first operand
     * @param right the second operand
     * @return the operator result
     */
    double applyAsDoubleThrow(double left, double right) throws E;

    /**
     * Applies this operator to the given operands.
     *
     * @param left  the first operand
     * @param right the second operand
     * @return the operator result
     */
    @Override
    default double applyAsDouble(double left, double right) {
        try {
            return applyAsDoubleThrow(left, right);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }
}
