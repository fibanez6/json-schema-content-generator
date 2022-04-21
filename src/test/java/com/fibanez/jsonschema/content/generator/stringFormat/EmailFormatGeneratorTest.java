package com.fibanez.jsonschema.content.generator.stringFormat;

import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.internal.EmailFormatValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class EmailFormatGeneratorTest {

    private final FormatValidator formatValidator = new EmailFormatValidator();

    @Test
    void shouldReturnValidString() {
        FormatGenerator generator = new EmailFormatGenerator();
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }

}