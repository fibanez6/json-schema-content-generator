package com.fibanez.jsonschema.content.generator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.everit.json.schema.NullSchema;
import org.json.JSONObject;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class NullSchemaGenerator implements SchemaGenerator<NullSchema> {

    @Override
    public Object generate(@NonNull NullSchema schema, JsonNode jsonNode) {
        return JSONObject.NULL;
    }
}
