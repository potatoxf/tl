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
import java.util.function.IntUnaryOperator;

/**
 * Represents an operation on a single {@code int}-valued operand that produces an {@code
 * int}-valued result. This is the primitive type specialization of {@link UnaryOperatorThrow} for
 * {@code int}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsInt(int)}.
 *
 * @see UnaryOperatorThrow
 */
@FunctionalInterface
public interface IntUnaryOperatorThrow<E extends Throwable> extends IntUnaryOperator {

    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @return a unary operator that always returns its input argument
     */
    static <E extends Throwable> IntUnaryOperatorThrow<E> identity() {
        return t -> t;
    }

    /**
     * Applies this operator to the given operand.
     *
     * @param operand the operand
     * @return the operator result
     */
    int applyAsIntThrow(int operand) throws E;

    /**
     * Applies this operator to the given operand.
     *
     * @param operand the operand
     * @return the operator result
     */
    @Override
    default int applyAsInt(int operand) {
        try {
            return applyAsIntThrow(operand);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }

    /**
     * Returns a composed operator that first applies the {@code before} operator to its input, and
     * then applies this operator to the result. If evaluation of either operator throws an exception,
     * it is relayed to the caller of the composed operator.
     *
     * @param before the operator to apply before this operator is applied
     * @return a composed operator that first applies the {@code before} operator and then applies
     * this operator
     * @throws NullPointerException if before is null
     * @see #andThen(IntUnaryOperatorThrow)
     */
    default IntUnaryOperatorThrow<E> compose(IntUnaryOperatorThrow<? extends E> before) {
        Objects.requireNonNull(before);
        return (int v) -> applyAsInt(before.applyAsInt(v));
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
     * @see #compose(IntUnaryOperatorThrow)
     */
    default IntUnaryOperatorThrow<E> andThen(IntUnaryOperatorThrow<? extends E> after) {
        Objects.requireNonNull(after);
        return (int t) -> after.applyAsInt(applyAsInt(t));
    }
}
