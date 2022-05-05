package com.fibanez.jsonschema.content.generator.javaType;

import com.fibanez.jsonschema.content.generator.Generator;

public class ConstantGenerator<T> implements Generator<T> {

    private final T value;

    public ConstantGenerator(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }
}
