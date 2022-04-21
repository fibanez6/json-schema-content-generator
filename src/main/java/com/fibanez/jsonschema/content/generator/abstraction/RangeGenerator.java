package com.fibanez.jsonschema.content.generator.abstraction;

/**
 * Represents a function that accepts two arguments and produces a result.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #get(Object, Object)}.
 *
 * @param <T> the type of the first and S]second arguments to the function
 * @param <R> the type of the result of the function
 *
 */
@FunctionalInterface
public interface RangeGenerator<T, R> {

    R get(T left, T right);

}
