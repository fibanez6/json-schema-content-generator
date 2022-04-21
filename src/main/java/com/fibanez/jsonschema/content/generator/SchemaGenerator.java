package com.fibanez.jsonschema.content.generator;


import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.util.ReflectionUtils;
import lombok.NonNull;
import org.everit.json.schema.Schema;

@FunctionalInterface
public interface SchemaGenerator<S extends Schema> {

    Object generate(@NonNull S schema, CrumbPath crumbPath);

    @SuppressWarnings("unchecked")
    default Class<S> schemaType() {
        return (Class<S>) ReflectionUtils.getClassType(this.getClass());
    }

    default Object generateFrom(@NonNull Schema schema, @NonNull CrumbPath crumbPath) {
        SchemaGenerator innerGenerator = Context.getSchemaGenerator(schema.getClass());
        return innerGenerator.generate(schema, crumbPath);
    }

    default <T> T getOrDefault(T value, T defaultValue) {
        return (value != null) ? value : defaultValue;
    }
}
