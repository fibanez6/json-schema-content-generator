package com.fibanez.jsonschema.content.generator.abstraction;

import java.util.Set;

public interface MultipleOfGenerator<T extends Number> extends RangeGenerator<T, T> {

    T get(T min, T max, T multipleOf);

    T get(T min, T max, T multipleOf, Set<Number> shouldNotMultipleOf);

}
