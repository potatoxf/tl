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

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

/**
 * Represents an operation on a single {@code double}-valued operand that produces a {@code
 * double}-valued result. This is the primitive type specialization of {@link UnaryOperatorThrow}
 * for {@code double}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsDouble(double)}.
 *
 * @see UnaryOperatorThrow
 */
@FunctionalInterface
public interface DoubleUnaryOperatorThrow<E extends Throwable> extends DoubleUnaryOperator {

    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @return a unary operator that always returns its input argument
     */
    static <E extends Throwable> DoubleUnaryOperatorThrow<E> identity() {
        return t -> t;
    }

    /**
     * Applies this operator to the given operand.
     *
     * @param operand the operand
     * @return the operator result
     */
    double applyAsDouble(double operand);

    /**
     * Returns a composed operator that first applies the {@code before} operator to its input, and
     * then applies this operator to the result. If evaluation of either operator throws an exception,
     * it is relayed to the caller of the composed operator.
     *
     * @param before the operator to apply before this operator is applied
     * @return a composed operator that first applies the {@code before} operator and then applies
     * this operator
     * @throws NullPointerException if before is null
     * @see #andThen(DoubleUnaryOperatorThrow)
     */
    default DoubleUnaryOperatorThrow<E> compose(DoubleUnaryOperatorThrow<? extends E> before) {
        Objects.requireNonNull(before);
        return (double v) -> applyAsDouble(before.applyAsDouble(v));
    }

    /**
     * Returns a composed operator that first applies this operator to its input, and then applies the
     * {@code after} operator to the result. If evaluation of either operator throws an exception, it
     * is relayed to the caller of the composed operator.
     *
     * @param after the operator to apply after this operator is applied
     * @return a composed operator that first applies this operator and then applies the {@code after}
     * operator
     * @throws NullPointerException if after is null
     * @see #compose(DoubleUnaryOperatorThrow)
     */
    default DoubleUnaryOperatorThrow<E> andThen(DoubleUnaryOperatorThrow<? extends E> after) {
        Objects.requireNonNull(after);
        return (double t) -> after.applyAsDouble(applyAsDouble(t));
    }
}
