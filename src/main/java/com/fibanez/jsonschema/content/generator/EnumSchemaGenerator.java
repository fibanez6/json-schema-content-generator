package com.fibanez.jsonschema.content.generator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.everit.json.schema.EnumSchema;
import org.json.JSONObject;

import java.util.Optional;

import static com.fibanez.jsonschema.content.generator.util.RandomUtils.RANDOM;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class EnumSchemaGenerator implements SchemaGenerator<EnumSchema> {

    @Override
    public Object generate(@NonNull EnumSchema schema, JsonNode jsonNode) {
        if (schema.getPossibleValues() == null || schema.getPossibleValues().isEmpty()) {
            return JSONObject.NULL;
        }

        int skip = RANDOM.nextInt(schema.getPossibleValues().size());
        Optional<Object> value = schema.getPossibleValues().stream()
                .skip(skip)
                .findFirst();
        return value.orElse(JSONObject.NULL);
    }
}
