package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.abstraction.MultipleOfGenerator;
import com.fibanez.jsonschema.content.generator.abstraction.RangeGenerator;
import com.fibanez.jsonschema.content.generator.javaType.JavaTypeGenerator;
import lombok.NonNull;
import org.everit.json.schema.NumberSchema;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public final class NumberSchemaGenerator implements SchemaGenerator<NumberSchema> {

    @Override
    public Number generate(@NonNull NumberSchema schema, CrumbPath crumbPath) {
        JavaTypeGenerator<Integer> generator = Context.getJavaTypeGenerator(Integer.class);

        Number minValue = getMinValue(schema).orElse(Context.context().getNumberMin());
        Number maxValue = getMaxValue(schema).orElse(Context.context().getNumberMax());

        if (generator instanceof MultipleOfGenerator) {
            MultipleOfGenerator<Integer> numberTypeGenerator = (MultipleOfGenerator<Integer>) generator;
            Number multipleOf = getMultipleOfValue(schema).orElse(Context.DEFAULT_NUMBER_MULTIPLE_OF);
            return numberTypeGenerator.get(minValue.intValue(), maxValue.intValue(), multipleOf.intValue());
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
}
