/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.util.Comparator;
import java.util.Objects;

/**
 * Represents an operation upon two operands of the same type, producing a result of the same type
 * as the operands. This is a specialization of {@link BiFunctionThrow} for the case where the
 * operands and the result are all of the same type.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #apply(Object, Object)}.
 *
 * @param <T> the type of the operands and result of the operator
 * @see BiFunctionThrow
 * @see UnaryOperatorThrow
 */
@FunctionalInterface
public interface BinaryOperatorThrow<T, E extends Throwable> extends BiFunctionThrow<T, T, T, E> {
    /**
     * Returns a {@link BinaryOperatorThrow} which returns the lesser of two elements according to the
     * specified {@code Comparator}.
     *
     * @param <T>        the type of the input arguments of the comparator
     * @param comparator a {@code Comparator} for comparing the two values
     * @return a {@code BinaryOperator} which returns the lesser of its operands, according to the
     * supplied {@code Comparator}
     * @throws NullPointerException if the argument is null
     */
    static <T, E extends Throwable> BinaryOperatorThrow<T, E> minBy(
            Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return (a, b) -> comparator.compare(a, b) <= 0 ? a : b;
    }

    /**
     * Returns a {@link BinaryOperatorThrow} which returns the greater of two elements according to
     * the specified {@code Comparator}.
     *
     * @param <T>        the type of the input arguments of the comparator
     * @param comparator a {@code Comparator} for comparing the two values
     * @return a {@code BinaryOperator} which returns the greater of its operands, according to the
     * supplied {@code Comparator}
     * @throws NullPointerException if the argument is null
     */
    static <T, E extends Throwable> BinaryOperatorThrow<T, E> maxBy(
            Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return (a, b) -> comparator.compare(a, b) >= 0 ? a : b;
    }
}
