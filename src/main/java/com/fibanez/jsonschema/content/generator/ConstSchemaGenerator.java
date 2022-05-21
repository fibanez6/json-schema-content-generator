package com.fibanez.jsonschema.content.generator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.everit.json.schema.ConstSchema;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ConstSchemaGenerator implements SchemaGenerator<ConstSchema> {

    @Override
    public Object generate(@NonNull ConstSchema schema, JsonNode jsonNode) {
        return schema.getPermittedValue();
    }

}
