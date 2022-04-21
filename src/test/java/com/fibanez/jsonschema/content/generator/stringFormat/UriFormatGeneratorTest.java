package com.fibanez.jsonschema.content.generator.stringFormat;

import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.internal.URIFormatValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class UriFormatGeneratorTest {

    private final FormatValidator formatValidator = new URIFormatValidator();

    @Test
    void shouldReturnValidString() {
        FormatGenerator generator = new UriFormatGenerator();
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }
}