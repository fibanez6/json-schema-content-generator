package com.fibanez.jsonschema.content.validation;

import com.fibanez.jsonschema.content.validator.DurationFormatValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DurationFormatValidatorTest extends PatternFormatValidatorTest {

    private static final String DURATION_FORMAT = "^P(?:(?:\\d+D|\\d+M(?:\\d+D)?|\\d+Y(?:\\d+M(?:\\d+D)?)?)(?:T(?:\\d+H(?:\\d+M(?:\\d+S)?)?|\\d+M(?:\\d+S)?|\\d+S))?|T(?:\\d+H(?:\\d+M(?:\\d+S)?)?|\\d+M(?:\\d+S)?|\\d+S)|\\d+W)$";

    public DurationFormatValidatorTest() {
        super(DURATION_FORMAT, new DurationFormatValidator());
    }

    @Test
    void validDuration() {
        Optional<String> validationResult = validator.validate(Duration.ofSeconds(1).toString());
        validationResult.ifPresent(Assertions::fail);

    }

    @ParameterizedTest
    @ValueSource(strings = { "PT0S", "PT36H", "PT1M", "P5Y", "P1DT12H" })
    void validDuration(String duration) {
        Optional<String> validationResult = validator.validate(duration);
        validationResult.ifPresent(Assertions::fail);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "P3X", "P0,5Y", "P0.5Y", "AbC" })
    void shouldReturnError(String duration) {
        Optional<String> result = validator.validate(duration);
        assertEquals(getFormatErrorMsg(duration), result.get());
    }

}
