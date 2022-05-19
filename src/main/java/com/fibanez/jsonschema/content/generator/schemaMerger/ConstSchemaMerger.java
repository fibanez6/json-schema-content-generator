package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import org.everit.json.schema.ConstSchema;
import org.everit.json.schema.Schema;

final class ConstSchemaMerger implements SchemaMerger {

    private final ConstSchema.ConstSchemaBuilder schemaBuilder;

    ConstSchemaMerger() {
        this.schemaBuilder = new ConstSchema.ConstSchemaBuilder();
    }

    @Override
    public Schema getSchema() {
        return schemaBuilder.build();
    }

    @Override
    public ConstSchemaMerger combine(Schema schema) {
        if (schema instanceof ConstSchema) {
            doCombine((ConstSchema) schema);
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema);
        }
        return this;
    }

    @Override
    public ConstSchemaMerger not(Schema schema) {
        if (schema instanceof ConstSchema) {
            doNot((ConstSchema) schema);
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema);
        }
        return this;
    }

    private void doCombine(ConstSchema schema) {
        if (schema.getDefaultValue() != null) {
            schemaBuilder.defaultValue(schema.getDefaultValue());
        }
        if (schema.getPermittedValue() != null) {
            schemaBuilder.permittedValue(schema.getPermittedValue());
        }
    }

    private void doNot(ConstSchema schema) {
        if (schema.getPermittedValue() != null) {
            String originalPermittedValue = String.valueOf(schemaBuilder.build().getPermittedValue());
            String toDeniedPermittedValue = String.valueOf(schema.getPermittedValue());
            if (originalPermittedValue.equals(toDeniedPermittedValue)) {
                schemaBuilder.permittedValue("");
            }
        }
    }

}
