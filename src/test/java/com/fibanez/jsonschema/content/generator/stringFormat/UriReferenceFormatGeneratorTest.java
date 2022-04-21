package com.fibanez.jsonschema.content.generator.stringFormat;

import com.fibanez.jsonschema.content.generator.Generator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UriReferenceFormatGeneratorTest {

    @Test
    void shouldThrowException() {
        assertThrows(UnsupportedOperationException.class, () ->{
            Generator<String> generator = new UriReferenceFormatGenerator();
            generator.get();
        });
    }

}