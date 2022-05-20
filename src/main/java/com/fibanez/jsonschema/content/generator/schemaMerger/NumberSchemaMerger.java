package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import org.everit.json.schema.NumberSchema;
import org.everit.json.schema.Schema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.fibanez.jsonschema.content.Context.UNPROCESSED_NOT_MULTIPLE_OF;

final class NumberSchemaMerger implements SchemaMerger {

    private final NumberSchema.Builder schemaBuilder;

    NumberSchemaMerger() {
        this.schemaBuilder = new NumberSchema.Builder();
    }

    @Override
    public SchemaMerger combine(Schema schema) {
        if (schema instanceof NumberSchema) {
            doCombine((NumberSchema) schema);
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema.getClass());
        }
        return this;
    }

    @Override
    public NumberSchemaMerger not(Schema schema) {
        if (schema instanceof NumberSchema) {
            doNot((NumberSchema) schema);
        } else {
            throw new GeneratorException("Unsupported negate schema '%s'", schema.getClass());
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

    private void doNot(NumberSchema toNegateSchema) {
        NumberSchema current = getSchema();

        Number minimum = getMinNumber(current.getMinimum(), current.getMaximum(), toNegateSchema.getMaximum());
        Number maximum = getMaxNumber(minimum, current.getMaximum(), toNegateSchema.getMinimum());

        schemaBuilder.minimum(minimum);
        schemaBuilder.maximum(maximum);

        if (toNegateSchema.getMultipleOf() != null) {
            // Copy all unprocessed Props
            Map<String, Object> copyUnprocessed = new HashMap<>(current.getUnprocessedProperties());
            appendNotMultipleOf(copyUnprocessed, toNegateSchema);
            schemaBuilder.unprocessedProperties(copyUnprocessed);
        }

    }

    private Number getMinNumber(Number minimum, Number maximum, Number minNegated) {
        if (minNegated == null) {
            return minimum;
        }
        if (maximum != null && compare(minNegated, maximum) > 0) {
            return minimum;
        }
        return minNegated;
    }

    private Number getMaxNumber(Number minimum, Number maximum, Number maxNegated) {
        if (maxNegated == null) {
            return maximum;
        }
        if (minimum != null && compare(minimum, maxNegated) > 0) {
            return maximum;
        }
        return maxNegated;
    }

    @SuppressWarnings("unchecked")
    private void appendNotMultipleOf(Map<String, Object> unprocessed, NumberSchema schema) {
        unprocessed.putIfAbsent(UNPROCESSED_NOT_MULTIPLE_OF, new HashSet<>());
        Set<Number> notMultipleOfs = ((Set<Number>) unprocessed.get(UNPROCESSED_NOT_MULTIPLE_OF));
        notMultipleOfs.add(schema.getMultipleOf());
    }

    private int compare(Number a, Number b) {
        if (a instanceof Double) {
            return Double.compare(a.doubleValue(), b.doubleValue());
        } else if (a instanceof Float) {
            return Float.compare(a.floatValue(), b.floatValue());
        } else if (a instanceof Long) {
            return Long.compare(a.longValue(), b.longValue());
        } else {
            return Integer.compare(a.intValue(), b.intValue());
        }
    }

}
