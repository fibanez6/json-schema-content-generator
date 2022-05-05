package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.abstraction.MultipleOfGenerator;
import com.fibanez.jsonschema.content.generator.abstraction.RangeGenerator;
import com.fibanez.jsonschema.content.generator.javaType.JavaTypeGenerator;
import lombok.NonNull;
import org.everit.json.schema.NumberSchema;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.ofNullable;

public final class NumberSchemaGenerator implements SchemaGenerator<NumberSchema> {

    @Override
    public Number generate(@NonNull NumberSchema schema, JsonNode jsonNode) {
        JavaTypeGenerator<Integer> generator = Context.getJavaTypeGenerator(Integer.class);

        Number minValue = getMinValue(schema).orElse(Context.current().getNumberMin());
        Number maxValue = getMaxValue(schema).orElse(Context.current().getNumberMax());

        if (generator instanceof MultipleOfGenerator) {
            MultipleOfGenerator<Integer> numberTypeGenerator = (MultipleOfGenerator<Integer>) generator;
            Number multipleOf = getMultipleOfValue(schema).orElse(Context.DEFAULT_NUMBER_MULTIPLE_OF);

            // Check for non-multipleOf
            Set<Number> negatedMultipleOf = getNegatedMultipleOfValue(schema);
            if (negatedMultipleOf.isEmpty()) {
                return numberTypeGenerator.get(minValue.intValue(), maxValue.intValue(), multipleOf.intValue());
            } else {
                return numberTypeGenerator.get(minValue.intValue(), maxValue.intValue(), multipleOf.intValue(), negatedMultipleOf);
            }

        } else if (generator instanceof RangeGenerator) {
            RangeGenerator<Integer, Integer> numberTypeGenerator = (RangeGenerator<Integer, Integer>) generator;
            return numberTypeGenerator.get(minValue.intValue(), maxValue.intValue());
        }

        return generator.get();
    }

    private Optional<Number> getMinValue(NumberSchema schema) {
        Number value = getOrDefault(schema.getMinimum(), schema.getExclusiveMinimumLimit());
        return ofNullable(value);
    }

    private Optional<Number> getMaxValue(NumberSchema schema) {
        Number value = getOrDefault(schema.getMaximum(), schema.getExclusiveMaximumLimit());
        return ofNullable(value);
    }

    private Optional<Number> getMultipleOfValue(NumberSchema schema) {
        return ofNullable(schema.getMultipleOf());
    }

    @SuppressWarnings("unchecked")
    private Set<Number> getNegatedMultipleOfValue(NumberSchema schema) {
        return (Set<Number>) schema.getUnprocessedProperties().getOrDefault(Context.UNPROCESSED_NOT_MULTIPLE_OF, Collections.emptySet());
    }

}
