package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import lombok.Getter;
import org.everit.json.schema.NumberSchema;
import org.everit.json.schema.Schema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.fibanez.jsonschema.content.Context.UNPROCESSED_NOT_MULTIPLE_OF;

@Getter
class NumberSchemaMerger implements SchemaMerger {

    private final NumberSchema.Builder schemaBuilder;

    NumberSchemaMerger(NumberSchema schema) {
        this.schemaBuilder = new NumberSchema.Builder();
        combine(schema);
    }

    @Override
    public NumberSchemaMerger combine(Schema schema) {
        if (schema instanceof NumberSchema) {
            doCombine((NumberSchema) schema);
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema);
        }
        return this;
    }

    @Override
    public NumberSchemaMerger not(Schema schema) {
        if (schema instanceof NumberSchema) {
            doNot((NumberSchema) schema);
        } else {
            throw new GeneratorException("Unsupported negate schema '%s'", schema);
        }
        return this;
    }

    @Override
    public NumberSchema getSchema() {
        return schemaBuilder.build();
    }

    private void doCombine(NumberSchema schema) {
        if (schema.getMinimum() != null) {
            schemaBuilder.minimum(schema.getMinimum());
        }
        if (schema.getMaximum() != null) {
            schemaBuilder.maximum(schema.getMaximum());
        }
        if (schema.getMultipleOf() != null) {
            schemaBuilder.multipleOf(schema.getMultipleOf());
        }
        if (schema.getExclusiveMinimumLimit() != null) {
            schemaBuilder.exclusiveMinimum(schema.getExclusiveMinimumLimit());
        }
        if (schema.getExclusiveMaximumLimit() != null) {
            schemaBuilder.exclusiveMaximum(schema.getExclusiveMaximumLimit());
        }
        Map<String, Object> unprocessedProperties = schema.getUnprocessedProperties();
        if (unprocessedProperties != null && !unprocessedProperties.isEmpty()) {
            schemaBuilder.unprocessedProperties(unprocessedProperties);
        }
        schemaBuilder.exclusiveMinimum(schema.isExclusiveMinimum());
        schemaBuilder.exclusiveMaximum(schema.isExclusiveMaximum());
        schemaBuilder.requiresNumber(schema.isRequiresNumber());
        schemaBuilder.requiresInteger(schema.requiresInteger());
    }

    private void doNot(NumberSchema schema) {
        NumberSchema current = getSchema();

        if (schema.getMinimum() != null) {
            schemaBuilder.maximum(schema.getMinimum());
            if (current.getMaximum() == null) {
                schemaBuilder.minimum(null);
            }
        }
        if (schema.getMaximum() != null) {
            schemaBuilder.minimum(schema.getMaximum());
            if (current.getMinimum() == null) {
                schemaBuilder.maximum(null);
            }
        }

        if (schema.getMultipleOf() != null) {
            // Copy all unprocessed Props
            Map<String, Object> copyUnprocessed = new HashMap<>(current.getUnprocessedProperties());
            appendNotMultipleOf(copyUnprocessed, schema);
            schemaBuilder.unprocessedProperties(copyUnprocessed);
        }

    }

    @SuppressWarnings("unchecked")
    private void appendNotMultipleOf(Map<String, Object> unprocessed, NumberSchema schema) {
        unprocessed.putIfAbsent(UNPROCESSED_NOT_MULTIPLE_OF, new HashSet<>());
        Set<Number> notMultipleOfs = ((Set<Number>) unprocessed.get(UNPROCESSED_NOT_MULTIPLE_OF));
        notMultipleOfs.add(schema.getMultipleOf());
    }

}
