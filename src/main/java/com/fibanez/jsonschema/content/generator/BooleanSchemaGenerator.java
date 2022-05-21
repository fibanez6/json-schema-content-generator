package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.everit.json.schema.BooleanSchema;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class BooleanSchemaGenerator implements SchemaGenerator<BooleanSchema> {

    @Override
    public Boolean generate(@NonNull BooleanSchema schema, JsonNode jsonNode) {
        Generator<Boolean> generator = Context.getJavaTypeGenerator(Boolean.class);
        return generator.get();
    }
}
