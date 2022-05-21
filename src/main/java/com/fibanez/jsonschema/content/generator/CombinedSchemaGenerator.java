package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.generator.schemaMerger.SchemaCombinator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.Schema;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class CombinedSchemaGenerator implements SchemaGenerator<CombinedSchema> {

    @Override
    public Object generate(@NonNull CombinedSchema schema, @NonNull JsonNode jsonNode) {
        Schema combined = SchemaCombinator.combine(schema);
        return generateFrom(combined, jsonNode);
    }

}