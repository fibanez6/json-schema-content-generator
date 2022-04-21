package com.fibanez.jsonschema.content.generator.javaType;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.abstraction.MultipleOfGenerator;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

public final class IntegerGenerator implements JavaTypeGenerator<Integer>, MultipleOfGenerator<Integer> {

    @Override
    public Integer get() {
        Context context = Context.context();
        int min = context.getNumberMin().intValue();
        int max = context.getNumberMax().intValue();
        int multipleOf = Context.DEFAULT_NUMBER_MULTIPLE_OF.intValue();
        return get(min, max, multipleOf);
    }

    @Override
    public Integer get(@NonNull Integer min, @NonNull Integer max) {
        return get(min, max, Context.DEFAULT_NUMBER_MULTIPLE_OF.intValue());
    }

    @Override
    public Integer get(@NonNull Integer min, @NonNull Integer max, Integer multipleOf) {
        Validate.isTrue(min <= max, "To value must be smaller or equal to from value.");
        Validate.isTrue(multipleOf > 0, "MultipleOf must be a positive value");

        int rightRange = max;
        int leftRange = min;

        if (multipleOf > 1) {
            // find the amount of multiples m between a/m and b/m
            int n = (rightRange / multipleOf - leftRange / multipleOf) + (leftRange % multipleOf == 0 ? 1 : 0);
            Validate.isTrue(n > 0, "No possible multipleOf %d in range [%d,%d]", multipleOf, leftRange, rightRange);
            // find first multipleOf after leftRange
            if (leftRange % multipleOf != 0) {
                leftRange = multipleOf * (leftRange / multipleOf + 1);
            }
        }
        return leftRange + RandomUtils.nextInt((rightRange - leftRange) / multipleOf) * multipleOf;
    }
}
