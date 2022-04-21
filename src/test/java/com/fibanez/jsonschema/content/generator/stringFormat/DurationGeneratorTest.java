package com.fibanez.jsonschema.content.generator.stringFormat;

import com.fibanez.jsonschema.content.validator.DurationFormatValidator;
import org.everit.json.schema.FormatValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DurationGeneratorTest {

    private final FormatValidator formatValidator = new DurationFormatValidator();

    @Test
    void shouldThrowException_fromIsNull() {
        assertThrows(NullPointerException.class, () -> new DurationFormatGenerator(null, Duration.ZERO));
    }

    @Test
    void shouldThrowException_toIsNull() {
        assertThrows(NullPointerException.class, () -> new DurationFormatGenerator(Duration.ZERO, null));
    }

    @RepeatedTest(10)
    void shouldReturnValidString() {
        FormatGenerator generator = new DurationFormatGenerator(Duration.ZERO, Duration.ofDays(1));
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }

}