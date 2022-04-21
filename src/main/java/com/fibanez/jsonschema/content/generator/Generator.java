package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.generator.util.ReflectionUtils;

import java.util.function.Supplier;

public interface Generator<T> extends Supplier<T>  {

    @SuppressWarnings("unchecked")
    default Class<T> classType() {
        return (Class<T>) ReflectionUtils.getClassType(this.getClass());
    }
}
