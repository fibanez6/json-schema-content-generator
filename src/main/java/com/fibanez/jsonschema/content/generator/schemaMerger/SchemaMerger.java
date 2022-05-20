package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import lombok.NonNull;
import org.everit.json.schema.ConstSchema;
import org.everit.json.schema.EnumSchema;
import org.everit.json.schema.NotSchema;
import org.everit.json.schema.NumberSchema;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;

/**
 * Merge 2 compatible schemas
 */
public interface SchemaMerger {

    SchemaMerger combine(Schema schema);

    SchemaMerger not(Schema schema);

    Schema getSchema();

    static SchemaMerger forSchema(@NonNull Schema schema) {
        if (schema instanceof StringSchema) {
            return new StringSchemaMerger();
        } else if (schema instanceof NumberSchema) {
            return new NumberSchemaMerger();
        } else if (schema instanceof ObjectSchema) {
            return new ObjectSchemaMerger();
        } else if (schema instanceof ConstSchema) {
            return new ConstSchemaMerger();
        } else if (schema instanceof EnumSchema) {
            return new EnumSchemaMerger();
        } else if (schema instanceof NotSchema) {
            return new NotSchemaMerger();
        }
        throw new GeneratorException("Unsupported merge schema '%s'", schema.getClass());
    }

}
