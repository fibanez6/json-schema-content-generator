package com.fibanez.jsonschema.content.generator.javaType;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.abstraction.RangeGenerator;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

public final class StringGenerator implements JavaTypeGenerator<String>, RangeGenerator<Integer, String> {

    @Override
    public String get() {
        Context context = Context.context();
        int minLength = context.getStringLengthMin();
        int maxLength = context.getStringLengthMax();
        return get(minLength, maxLength);
    }

    @Override
    public String get(@NonNull Integer minLength, @NonNull Integer maxLength) {
        Validate.isTrue(minLength <= maxLength, "Min length must be smaller or equal to Max length.");
        return RandomUtils.string(minLength, maxLength);
    }

}
