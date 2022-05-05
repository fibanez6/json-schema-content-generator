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

    private void doNot(NumberSchema toNegateSchema) {
        NumberSchema current = getSchema();

        Number newMin = negateMax(current, toNegateSchema);
        Number newMax = negateMin(current, toNegateSchema);

        schemaBuilder.minimum(newMin);
        schemaBuilder.maximum(newMax);

        // Correction
        if (newMin != null && newMax != null && compare(newMin, newMax) > 0) {
            schemaBuilder.maximum(null);
        }

        if (toNegateSchema.getMultipleOf() != null) {
            // Copy all unprocessed Props
            Map<String, Object> copyUnprocessed = new HashMap<>(current.getUnprocessedProperties());
            appendNotMultipleOf(copyUnprocessed, toNegateSchema);
            schemaBuilder.unprocessedProperties(copyUnprocessed);
        }

    }

    private Number negateMin(NumberSchema current, NumberSchema toNegateSchema) {
        if (toNegateSchema.getMinimum() == null) {
            return current.getMaximum();
        }
        if (current.getMaximum() == null) {
            return toNegateSchema.getMinimum();
        }
        return getMin(current.getMaximum(), toNegateSchema.getMinimum());
    }

    private Number negateMax(NumberSchema current, NumberSchema toNegateSchema) {
        if (toNegateSchema.getMaximum() == null) {
            return current.getMinimum();
        }
        if (current.getMinimum() == null) {
            return toNegateSchema.getMaximum();
        }
        return getMax(current.getMinimum(), toNegateSchema.getMaximum());

    }

    @SuppressWarnings("unchecked")
    private void appendNotMultipleOf(Map<String, Object> unprocessed, NumberSchema schema) {
        unprocessed.putIfAbsent(UNPROCESSED_NOT_MULTIPLE_OF, new HashSet<>());
        Set<Number> notMultipleOfs = ((Set<Number>) unprocessed.get(UNPROCESSED_NOT_MULTIPLE_OF));
        notMultipleOfs.add(schema.getMultipleOf());
    }

    private Number getMin(Number a, Number b) {
        if (a instanceof Double) {
            return Math.min(a.doubleValue(), b.doubleValue());
        } else if (a instanceof Float) {
            return Math.min(a.floatValue(), b.floatValue());
        } else if (a instanceof Long) {
            return Math.min(a.longValue(), b.longValue());
        } else {
            return Math.min(a.intValue(), b.intValue());
        }
    }

    private Number getMax(Number a, Number b) {
        if (a instanceof Double) {
            return Math.max(a.doubleValue(), b.doubleValue());
        } else if (a instanceof Float) {
            return Math.max(a.floatValue(), b.floatValue());
        } else if (a instanceof Long) {
            return Math.max(a.longValue(), b.longValue());
        } else {
            return Math.max(a.intValue(), b.intValue());
        }
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
