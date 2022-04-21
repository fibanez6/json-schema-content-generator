package com.fibanez.jsonschema.content.generator.stringFormat;

import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.internal.IPV6Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class Ipv6FormatGeneratorTest {

    private final FormatValidator formatValidator = new IPV6Validator();

    @Test
    void shouldReturnValidString() {
        FormatGenerator generator = new Ipv6FormatGenerator();
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }
}