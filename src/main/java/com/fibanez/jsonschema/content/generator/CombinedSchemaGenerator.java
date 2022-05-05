package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.generator.schemaMerger.SchemaMerger;
import lombok.NonNull;
import org.everit.json.schema.CombinedSchema;

public final class CombinedSchemaGenerator implements SchemaGenerator<CombinedSchema> {

    @Override
    public Object generate(@NonNull CombinedSchema schema, @NonNull JsonNode jsonNode) {
        SchemaMerger schemaMerger = SchemaMerger.forSchema(schema);
        return generateFrom(schemaMerger.getSchema(), jsonNode);
    }

}
