package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import org.everit.json.schema.NotSchema;
import org.everit.json.schema.Schema;

@Deprecated
public class NotSchemaMerger implements SchemaMerger {

    private final NotSchema.Builder schemaBuilder;

    NotSchemaMerger() {
        this.schemaBuilder = new NotSchema.Builder();
    }

    @Override
    public SchemaMerger combine(Schema schema) {
        if (schema instanceof NotSchema) {
            Schema mustNotMatch = ((NotSchema) schema).getMustNotMatch();
            SchemaMerger merger = SchemaMerger.forSchema(mustNotMatch);
            return merger.not(mustNotMatch);
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema.getClass());
        }
    }

    @Override
    public SchemaMerger not(Schema schema) {
        return SchemaMerger.forSchema(schema).not(schema);
    }

    @Override
    public NotSchema getSchema() {
        return schemaBuilder.build();
    }
}
