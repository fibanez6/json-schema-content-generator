package com.fibanez.jsonschema.content.generator.javaType;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.abstraction.MultipleOfGenerator;
import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.Set;

public final class IntegerGenerator implements JavaTypeGenerator<Integer>, MultipleOfGenerator<Integer> {

    @Override
    public Integer get() {
        Context context = Context.current();
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
            int n = findTotalMultipleOf(leftRange, rightRange, multipleOf);
            Validate.isTrue(n > 0, "No possible multipleOf %d in range [%d,%d]", multipleOf, leftRange, rightRange);
            // find first multipleOf after leftRange
            leftRange = findFirstMultipleOf(leftRange, multipleOf);
        }
        return leftRange + RandomUtils.nextInt((rightRange - leftRange) / multipleOf) * multipleOf;
    }

    @Override
    public Integer get(@NonNull Integer min, @NonNull Integer max, Integer multipleOf, Set<Number> shouldNotMultipleOf) {
        Integer candidate = get(min, max, multipleOf);

        boolean isNotMultipleOf = isNotMultipleOf(candidate, shouldNotMultipleOf);
        if (shouldNotMultipleOf.isEmpty() || isNotMultipleOf) {
            return candidate;
        }

        // Checking from first to pivot
        int leftRange = findFirstMultipleOf(min, multipleOf);
        Integer pivot = candidate;
        while (leftRange <= pivot) {
            if (isNotMultipleOf(pivot, shouldNotMultipleOf)) {
                return pivot;
            }
            pivot = pivot - multipleOf;
        }

        // Checking from pivot to last
        int rightRange = findLastMultipleOf(max, multipleOf);
        pivot = candidate;
        while (pivot <= rightRange) {
            if (isNotMultipleOf(pivot, shouldNotMultipleOf)) {
                return pivot;
            }
            pivot = pivot + multipleOf;
        }

        throw new GeneratorException("Could not find an integer that is multiple of %d and not a multiple of %s", multipleOf, shouldNotMultipleOf);
    }

    /**
     * Find the amount of multiples m between a/m and b/m
     */
    private int findTotalMultipleOf(int leftRange, int rightRange, int multipleOf) {
        return (rightRange / multipleOf - leftRange / multipleOf) + (leftRange % multipleOf == 0 ? 1 : 0);
    }

    /**
     * Find first multipleOf after leftRange
     */
    private int findFirstMultipleOf(int leftRange, int multipleOf) {
        if (leftRange % multipleOf != 0) {
            return multipleOf * (leftRange / multipleOf + 1);
        }
        return leftRange;
    }

    /**
     * Find last multipleOf after leftRange
     */
    private int findLastMultipleOf(int rightRange, int multipleOf) {
        if (rightRange % multipleOf != 0) {
            return multipleOf * (rightRange / multipleOf);
        }
        return rightRange;
    }

    /**
     * Check if candidate is multiple of a number from a collection
     */
    private boolean isNotMultipleOf(final Integer candidate, Set<Number> noMultipleOf) {
        return noMultipleOf.stream()
                .mapToInt(Number::intValue)
                .noneMatch(n -> candidate % n == 0);
    }

}
