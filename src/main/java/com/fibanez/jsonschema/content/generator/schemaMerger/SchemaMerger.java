package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import lombok.NonNull;
import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.ConditionalSchema;
import org.everit.json.schema.NumberSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;

/**
 * Merge 2 compatible schemas
 */
public interface SchemaMerger {

    SchemaMerger combine(Schema schema);

    SchemaMerger not(Schema schema);

    Schema getSchema();

    Schema.Builder getSchemaBuilder();

    static SchemaMerger forSchema(@NonNull Schema schema) {
        if (schema instanceof StringSchema) {
            return new StringSchemaMerger((StringSchema) schema);
        } else if (schema instanceof NumberSchema) {
            return new NumberSchemaMerger((NumberSchema) schema);
        } else if (schema instanceof CombinedSchema) {
            return new CombinedSchemaMerger((CombinedSchema) schema);
        } else if (schema instanceof ConditionalSchema) {
            return new ConditionalSchemaMerger((ConditionalSchema) schema);
        }
        throw new GeneratorException("Unsupported merge schema '%s'", schema);
    }

}
