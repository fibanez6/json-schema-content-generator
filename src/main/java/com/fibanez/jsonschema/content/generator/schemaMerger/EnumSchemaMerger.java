package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import org.everit.json.schema.ConstSchema;
import org.everit.json.schema.EnumSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;

import java.util.Collections;
import java.util.Set;

final class EnumSchemaMerger implements SchemaMerger {

    private final EnumSchema.Builder schemaBuilder;

    EnumSchemaMerger() {
        this.schemaBuilder = new EnumSchema.Builder();
    }

    @Override
    public EnumSchema getSchema() {
        return schemaBuilder.build();
    }

    @Override
    public SchemaMerger combine(Schema schema) {
        if (schema instanceof EnumSchema enumSchema) {
            doCombine(enumSchema);
        } else if (schema instanceof ConstSchema constSchema) {
            doCombine(constSchema);
        } else if (schema instanceof StringSchema) {
            // do nothing
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema.getClass());
        }
        return this;
    }

    @Override
    public EnumSchemaMerger not(Schema schema) {
        if (schema instanceof EnumSchema enumSchema) {
            doNot(enumSchema);
        } else if (schema instanceof ConstSchema constSchema) {
            doNot(constSchema);
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema.getClass());
        }
        return this;
    }

    private void doCombine(EnumSchema schema) {
        Set<Object> possibleValues = schema.getPossibleValues();
        if (possibleValues != null && !possibleValues.isEmpty()) {
            schemaBuilder.possibleValues(possibleValues);
        }
    }

    private void doCombine(ConstSchema schema) {
        if (schema.getPermittedValue() != null) {
            schemaBuilder.possibleValues(Collections.singleton(schema.getPermittedValue()));
        }
    }

    private void doNot(EnumSchema schema) {
        Set<Object> possibleValues = schemaBuilder.build().getPossibleValues();
        possibleValues.removeAll(schema.getPossibleValues());
        schemaBuilder.possibleValues(possibleValues);
    }

    private void doNot(ConstSchema schema) {
        EnumSchema builtSchema = schemaBuilder.build();
        Set<Object> possibleValues = builtSchema.getPossibleValues();
        possibleValues.remove(schema.getPermittedValue());
        if (possibleValues.isEmpty()) {
            possibleValues.add("null");
        }
        schemaBuilder.possibleValues(possibleValues);
    }

}
