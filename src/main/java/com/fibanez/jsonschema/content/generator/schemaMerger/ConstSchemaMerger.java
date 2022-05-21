package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.everit.json.schema.ConstSchema;
import org.everit.json.schema.Schema;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class ConstSchemaMerger implements SchemaMerger {

    private ConstSchema constSchema;

    @Override
    public ConstSchema getSchema() {
        return constSchema;
    }

    @Override
    public SchemaMerger combine(Schema schema) {
        if (schema instanceof ConstSchema) {
            this.constSchema = (ConstSchema) schema;
        } else if (constSchema != null) {
            return SchemaMerger.forSchema(schema).combine(schema).combine(constSchema);
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema.getClass());
        }
        return this;
    }

    @Override
    public ConstSchemaMerger not(Schema schema) {
        throw new GeneratorException("Unsupported merge schema '%s'", schema.getClass());
    }

}
