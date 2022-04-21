package com.fibanez.jsonschema.content.generator.javaType;

import com.fibanez.jsonschema.content.generator.util.RandomUtils;

public final class BooleanGenerator implements JavaTypeGenerator<Boolean> {

    @Override
    public Boolean get() {
        return RandomUtils.RANDOM.nextBoolean();
    }
}
