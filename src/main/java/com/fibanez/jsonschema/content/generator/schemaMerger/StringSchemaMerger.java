package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import lombok.Getter;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;

@Getter
class StringSchemaMerger implements SchemaMerger {

    private final StringSchema.Builder schemaBuilder;

    StringSchemaMerger(StringSchema schema) {
        this.schemaBuilder = new StringSchema.Builder();
        combine(schema);
    }

    @Override
    public StringSchemaMerger combine(Schema schema) {
        if (schema instanceof StringSchema) {
            doCombine((StringSchema) schema);
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema);
        }
        return this;
    }

    @Override
    public StringSchemaMerger not(Schema schema) {
        if (schema instanceof StringSchema) {
            doNot((StringSchema) schema);
        } else {
            throw new GeneratorException("Unsupported negate schema '%s'", schema);
        }
        return this;
    }

    @Override
    public StringSchema getSchema() {
        return schemaBuilder.build();
    }

    private void doCombine(StringSchema schema) {
        if (schema.getMinLength() != null) {
            schemaBuilder.minLength(schema.getMinLength());
        }
        if (schema.getMaxLength() != null) {
            schemaBuilder.maxLength(schema.getMaxLength());
        }
        if (schema.getPattern() != null) {
            schemaBuilder.pattern(schema.getPattern().pattern());
        }
        schemaBuilder.requiresString(schema.requireString());
    }

    private void doNot(StringSchema schema) {
        StringSchema current = getSchema();

        if (schema.getMinLength() != null) {
            schemaBuilder.maxLength(schema.getMinLength());
            if (current.getMaxLength() == null) {
                schemaBuilder.minLength(null);
            }
        }
        if (schema.getMaxLength() != null) {
            schemaBuilder.minLength(schema.getMaxLength());
            if (current.getMinLength() == null) {
                schemaBuilder.maxLength(null);
            }
        }
    }

}
