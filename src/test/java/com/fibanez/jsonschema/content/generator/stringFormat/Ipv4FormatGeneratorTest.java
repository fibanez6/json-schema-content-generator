package com.fibanez.jsonschema.content.generator.stringFormat;

import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.internal.IPV4Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class Ipv4FormatGeneratorTest {

    private final FormatValidator formatValidator = new IPV4Validator();

    @Test
    void shouldReturnValidString() {
        FormatGenerator generator = new Ipv4FormatGenerator();
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }
}