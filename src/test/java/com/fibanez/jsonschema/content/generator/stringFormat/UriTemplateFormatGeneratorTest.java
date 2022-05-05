package com.fibanez.jsonschema.content.generator.stringFormat;

import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.internal.URITemplateFormatValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class UriTemplateFormatGeneratorTest {

    private final FormatValidator formatValidator = new URITemplateFormatValidator();

    @Test
    void shouldReturnValidString() {
        FormatGenerator generator = new UriTemplateFormatGenerator();
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }
}