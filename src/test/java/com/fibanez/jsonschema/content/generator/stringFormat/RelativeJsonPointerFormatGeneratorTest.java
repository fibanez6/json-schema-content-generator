package com.fibanez.jsonschema.content.generator.stringFormat;

import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.internal.RelativeJsonPointerFormatValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class RelativeJsonPointerFormatGeneratorTest {

    private final FormatValidator formatValidator = new RelativeJsonPointerFormatValidator();

    @Test
    void shouldReturnValidString() {
        FormatGenerator generator = new RelativeJsonPointerFormatGenerator();
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }

}