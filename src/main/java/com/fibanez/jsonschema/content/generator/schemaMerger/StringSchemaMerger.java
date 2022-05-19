package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;

final class StringSchemaMerger implements SchemaMerger {

    private final StringSchema.Builder schemaBuilder;

    StringSchemaMerger() {
        this.schemaBuilder = new StringSchema.Builder();
    }

    @Override
    public StringSchemaMerger combine(Schema schema) {
        if (schema instanceof StringSchema) {
            doCombine((StringSchema) schema);
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema.getClass());
        }
        return this;
    }

    @Override
    public StringSchemaMerger not(Schema schema) {
        if (schema instanceof StringSchema) {
            doNot((StringSchema) schema);
        } else {
            throw new GeneratorException("Unsupported negate schema '%s'", schema.getClass());
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

        Integer minLength = getMinLength(current.getMinLength(), current.getMaxLength(), toNegateSchema.getMaxLength());
        Integer maxLength = getMaxLength(minLength, current.getMaxLength(), toNegateSchema.getMinLength());

        schemaBuilder.minLength(minLength);
        schemaBuilder.maxLength(maxLength);
    }

    private Integer getMinLength(Integer minLength, Integer maxLength, Integer minLengthNegated) {
        if (minLengthNegated == null) {
            return minLength;
        }
        if (maxLength != null && minLengthNegated > maxLength) {
            return minLength;
        }
        return minLengthNegated + 1;
    }

    private Integer getMaxLength(Integer minLength, Integer maxLength, Integer maxLengthNegated) {
        if (maxLengthNegated == null) {
            return maxLength;
        }
        if (minLength != null && minLength > maxLengthNegated) {
            return maxLength;
        }
        return maxLengthNegated;
    }

}
