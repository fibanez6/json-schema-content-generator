package com.fibanez.jsonschema.content.generator.stringFormat;

import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.internal.HostnameFormatValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class HostnameFormatGeneratorTest {

    private final FormatValidator formatValidator = new HostnameFormatValidator();

    @Test
    void shouldReturnValidString() {
        FormatGenerator generator = new HostnameFormatGenerator();
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }

}