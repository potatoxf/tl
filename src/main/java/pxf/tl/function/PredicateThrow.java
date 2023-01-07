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

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents a predicate (boolean-valued function) of one argument.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a> whose functional method is
 * {@link #test(Object)}.
 *
 * @param <T> the type of the input to the predicate
 */
@FunctionalInterface
public interface PredicateThrow<T, E extends Throwable> extends Predicate<T> {

    /**
     * Returns a predicate that tests if two arguments are equal according to {@link
     * Objects#equals(Object, Object)}.
     *
     * @param <T>       the type of arguments to the predicate
     * @param targetRef the object reference with which to compare for equality, which may be {@code
     *                  null}
     * @return a predicate that tests if two arguments are equal according to {@link
     * Objects#equals(Object, Object)}
     */
    static <T, E extends Throwable> PredicateThrow<T, E> isEqual(Object targetRef) {
        return (null == targetRef) ? Objects::isNull : targetRef::equals;
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
     */
    boolean testThrow(T t) throws E;

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
     */
    @Override
    default boolean test(T t) {
        try {
            return testThrow(t);
        } catch (Throwable e) {
            throw new FunctionException(e);
        }
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical AND of this predicate
     * and another. When evaluating the composed predicate, if this predicate is {@code false}, then
     * the {@code other} predicate is not evaluated.
     *
     * <p>Any exceptions thrown during evaluation of either predicate are relayed to the caller; if
     * evaluation of this predicate throws an exception, the {@code other} predicate will not be
     * evaluated.
     *
     * @param other a predicate that will be logically-ANDed with this predicate
     * @return a composed predicate that represents the short-circuiting logical AND of this predicate
     * and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default PredicateThrow<T, E> and(PredicateThrow<? super T, ? extends E> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }

    /**
     * Returns a predicate that represents the logical negation of this predicate.
     *
     * @return a predicate that represents the logical negation of this predicate
     */
    default PredicateThrow<T, E> negate() {
        return (t) -> !test(t);
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical OR of this predicate
     * and another. When evaluating the composed predicate, if this predicate is {@code true}, then
     * the {@code other} predicate is not evaluated.
     *
     * <p>Any exceptions thrown during evaluation of either predicate are relayed to the caller; if
     * evaluation of this predicate throws an exception, the {@code other} predicate will not be
     * evaluated.
     *
     * @param other a predicate that will be logically-ORed with this predicate
     * @return a composed predicate that represents the short-circuiting logical OR of this predicate
     * and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default PredicateThrow<T, E> or(PredicateThrow<? super T, ? extends E> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) || other.test(t);
    }
}
