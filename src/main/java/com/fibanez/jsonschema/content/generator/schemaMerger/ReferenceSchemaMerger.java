package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import org.everit.json.schema.ReferenceSchema;
import org.everit.json.schema.Schema;

public class ReferenceSchemaMerger implements SchemaMerger {

    @Override
    public SchemaMerger combine(Schema schema) {
        if (schema instanceof ReferenceSchema referenceSchema) {
            Schema referred = referenceSchema.getReferredSchema();
            return SchemaMerger.forSchema(referred).combine(referred);
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema.getClass());
        }
    }

    @Override
    public SchemaMerger not(Schema schema) {
        if (schema instanceof ReferenceSchema referenceSchema) {
            Schema referred = referenceSchema.getReferredSchema();
            return SchemaMerger.forSchema(referred).not(referred);
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema.getClass());
        }
    }

    @Override
    public Schema getSchema() {
        return null;
    }
}
