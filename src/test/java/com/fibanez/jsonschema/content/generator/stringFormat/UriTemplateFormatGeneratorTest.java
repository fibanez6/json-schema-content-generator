package com.fibanez.jsonschema.content.generator.stringFormat;

import com.fibanez.jsonschema.content.generator.Generator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UriTemplateFormatGeneratorTest {

    @Test
    void shouldThrowException() {
        assertThrows(UnsupportedOperationException.class, () ->{
            Generator<String> generator = new UriTemplateFormatGenerator();
            generator.get();
        });
    }
}