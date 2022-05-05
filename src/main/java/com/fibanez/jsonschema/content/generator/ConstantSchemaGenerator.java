package com.fibanez.jsonschema.content.generator;

import lombok.NonNull;
import org.everit.json.schema.ConstSchema;

public final class ConstantSchemaGenerator implements SchemaGenerator<ConstSchema> {

    @Override
    public Object generate(@NonNull ConstSchema schema, JsonNode jsonNode) {
        return schema.getPermittedValue();
    }

}
