package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import org.everit.json.schema.NotSchema;
import org.everit.json.schema.Schema;

public class NotSchemaMerger implements SchemaMerger {

    private Schema mustNotMatch;

    NotSchemaMerger(NotSchema schema) {
        this.mustNotMatch = schema.getMustNotMatch();
    }

    NotSchemaMerger() {
    }


    @Override
    public SchemaMerger combine(Schema schema) {
        if (schema instanceof NotSchema) {
            return SchemaMerger.forSchema(((NotSchema) schema).getMustNotMatch())
                    .not(((NotSchema) schema).getMustNotMatch());
        }
        throw new GeneratorException("Unsupported merge schema '%s'", schema);
    }

    @Override
    public SchemaMerger not(Schema schema) {
        if (schema instanceof NotSchema) {
            return SchemaMerger.forSchema(((NotSchema) schema).getMustNotMatch())
                    .combine(((NotSchema) schema).getMustNotMatch());
        }
        throw new GeneratorException("Unsupported merge schema '%s'", schema);
    }

    @Override
    public Schema getSchema() {
        return mustNotMatch;
    }
}
