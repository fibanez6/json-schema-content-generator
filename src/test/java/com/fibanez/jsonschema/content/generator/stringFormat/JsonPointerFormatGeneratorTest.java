package com.fibanez.jsonschema.content.generator.stringFormat;

import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.internal.JsonPointerFormatValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class JsonPointerFormatGeneratorTest {

    private final FormatValidator formatValidator = new JsonPointerFormatValidator();

    @Test
    void shouldReturnValidString() {
        FormatGenerator generator = new JsonPointerFormatGenerator();
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }

}