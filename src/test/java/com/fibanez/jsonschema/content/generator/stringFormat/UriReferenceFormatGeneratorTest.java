package com.fibanez.jsonschema.content.generator.stringFormat;

import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.internal.URIReferenceFormatValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class UriReferenceFormatGeneratorTest {

    private final FormatValidator formatValidator = new URIReferenceFormatValidator();

    @Test
    void shouldReturnValidString() {
        FormatGenerator generator = new UriReferenceFormatGenerator();
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }

}