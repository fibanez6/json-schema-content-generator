package com.fibanez.jsonschema.content.generator.abstraction;

public interface MultipleOfGenerator<T extends Number> extends RangeGenerator<T, T> {

    T get(T min, T max, T multipleOf);

}
