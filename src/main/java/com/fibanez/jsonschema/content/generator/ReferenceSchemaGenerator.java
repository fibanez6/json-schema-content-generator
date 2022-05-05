package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import lombok.NonNull;
import org.everit.json.schema.ReferenceSchema;
import org.everit.json.schema.Schema;

public class ReferenceSchemaGenerator implements SchemaGenerator<ReferenceSchema> {

    @Override
    public Object generate(@NonNull ReferenceSchema schema, @NonNull JsonNode jsonNode) {
        Schema referredSchema = schema.getReferredSchema();
        if (referredSchema == null) {
            throw new GeneratorException("No definition found for: " + jsonNode.getPath());
        }
        return generateFrom(referredSchema, jsonNode);
    }
}
