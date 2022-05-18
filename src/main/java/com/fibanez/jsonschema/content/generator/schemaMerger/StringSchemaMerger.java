package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import lombok.Getter;
import org.everit.json.schema.ConditionalSchema;
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
        } else if (schema instanceof ConditionalSchema) {
            SchemaMerger merger = SchemaMerger.forSchema(schema);
            combine(merger.getSchema());
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

    private void doNot(StringSchema toNegateSchema) {
        StringSchema current = getSchema();

        Integer newMinLength = negateMaxLength(current, toNegateSchema);
        Integer newMaxLength = negateMinLength(current, toNegateSchema);

        schemaBuilder.minLength(newMinLength);
        schemaBuilder.maxLength(newMaxLength);

        // Correction
        if (newMinLength != null && newMaxLength != null && newMinLength > newMaxLength) {
            schemaBuilder.maxLength(null);
        }
    }

    private Integer negateMinLength(StringSchema current, StringSchema toNegateSchema) {
        if (toNegateSchema.getMinLength() == null) {
            return current.getMaxLength();
        }
        if (current.getMaxLength() == null) {
            return toNegateSchema.getMinLength();
        }
        return toNegateSchema.getMinLength() > 0 ? toNegateSchema.getMinLength() : 1;
    }

    private Integer negateMaxLength(StringSchema current, StringSchema toNegateSchema) {
        if (toNegateSchema.getMaxLength() == null) {
            return current.getMinLength();
        }
        if (current.getMinLength() == null) {
            return toNegateSchema.getMaxLength();
        }
        return Math.min(toNegateSchema.getMaxLength() + 1, Context.DEFAULT_STRING_LENGTH_MAX);
    }

}
