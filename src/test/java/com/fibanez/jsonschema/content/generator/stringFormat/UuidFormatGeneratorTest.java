package com.fibanez.jsonschema.content.generator.stringFormat;

import com.fibanez.jsonschema.content.validator.UuidFormatValidator;
import org.everit.json.schema.FormatValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class UuidFormatGeneratorTest {

    private final FormatValidator formatValidator = new UuidFormatValidator();

    @Test
    void shouldReturnValidString() {
        FormatGenerator generator = new UuidFormatGenerator();
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }

}