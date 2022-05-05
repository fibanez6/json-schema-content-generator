package com.fibanez.jsonschema.content.generator.javaType;

import com.fibanez.jsonschema.content.generator.Generator;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;

import java.util.Collection;

public class CollectionGenerator<T> implements Generator<T> {

    private final Collection<T> values;

    public CollectionGenerator(Collection<T> values) {
        this.values = values;
    }

    @Override
    public T get() {
        return RandomUtils.nextElement(values);
    }
}
