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
import java.util.function.LongUnaryOperator;

/**
 * Represents an operation on a single {@code long}-valued operand that produces a {@code
 * long}-valued result. This is the primitive type specialization of {@link UnaryOperatorThrow} for
 * {@code long}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #applyAsLong(long)}.
 *
 * @see UnaryOperatorThrow
 */
@FunctionalInterface
public interface LongUnaryOperatorThrow<E extends Throwable> extends LongUnaryOperator {

    /**
     * Returns a unary operator that always returns its input argument.
     *
     * @return a unary operator that always returns its input argument
     */
    static <E extends Throwable> LongUnaryOperatorThrow<E> identity() {
        return t -> t;
    }

    /**
     * Applies this operator to the given operand.
     *
     * @param operand the operand
     * @return the operator result
     */
    long applyAsLongThrow(long operand) throws E;

    /**
     * Applies this operator to the given operand.
     *
     * @param operand the operand
     * @return the operator result
     */
    @Override
    default long applyAsLong(long operand) {
        try {
            return applyAsLongThrow(operand);
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
     * @see #andThen(LongUnaryOperatorThrow)
     */
    default LongUnaryOperatorThrow<E> compose(LongUnaryOperatorThrow<? extends E> before) {
        Objects.requireNonNull(before);
        return (long v) -> applyAsLong(before.applyAsLong(v));
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
     * @see #compose(LongUnaryOperatorThrow)
     */
    default LongUnaryOperatorThrow<E> andThen(LongUnaryOperatorThrow<? extends Throwable> after) {
        Objects.requireNonNull(after);
        return (long t) -> after.applyAsLong(applyAsLong(t));
    }
}
