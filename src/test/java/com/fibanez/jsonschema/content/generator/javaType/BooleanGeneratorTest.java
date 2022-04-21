package com.fibanez.jsonschema.content.generator.javaType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BooleanGeneratorTest {

    private final BooleanGenerator generator = new BooleanGenerator();

    @Test
    void shouldReturnRandomBoolean() {
        Boolean result = generator.get();
        assertNotNull(result);
    }

}