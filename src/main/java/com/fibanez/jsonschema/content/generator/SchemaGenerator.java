package com.fibanez.jsonschema.content.generator;


import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.util.ReflectionUtils;
import lombok.NonNull;
import org.everit.json.schema.Schema;

import java.util.Optional;
import java.util.function.Supplier;

@FunctionalInterface
public interface SchemaGenerator<S extends Schema> {

    Object generate(@NonNull S schema, JsonNode jsonNode);

    @SuppressWarnings("unchecked")
    default Class<S> schemaType() {
        return (Class<S>) ReflectionUtils.getClassType(this.getClass());
    }

    @SuppressWarnings("unchecked")
    default Object generateFrom(@NonNull Schema schema, @NonNull JsonNode jsonNode) {
        // Generates from predefined values generator if exists
        Optional<Supplier<?>> valueGenerator = Context.getPredefinedValueGenerator(jsonNode);
        if (valueGenerator.isPresent()) {
            return valueGenerator.get().get();
        }

        // Generates from schema generator
        SchemaGenerator innerGenerator = Context.getSchemaGenerator(schema.getClass());
        return innerGenerator.generate(schema, jsonNode);
    }

    default <T> T getOrDefault(T value, T defaultValue) {
        return (value != null) ? value : defaultValue;
    }
}
