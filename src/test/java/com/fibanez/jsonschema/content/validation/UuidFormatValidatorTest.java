package com.fibanez.jsonschema.content.validation;

import com.fibanez.jsonschema.content.validator.UuidFormatValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UuidFormatValidatorTest extends PatternFormatValidatorTest {

    private static final String UUID_FORMAT = "^[a-f0-9]{8}-[a-f0-9]{4}-[0-5][a-f0-9]{3}-[89ab][a-f0-9]{3}-[a-f0-9]{12}$";

    public UuidFormatValidatorTest() {
        super(UUID_FORMAT, new UuidFormatValidator());
    }

    @ParameterizedTest
    @ValueSource(strings = {
                    "4cc8f898-d0a5-098a-9264-2feeaa5879a9",
                    "4cc8f898-d0a5-198a-9264-2feeaa5879a9",
                    "4cc8f898-d0a5-298a-9264-2feeaa5879a9",
                    "4cc8f898-d0a5-398a-9264-2feeaa5879a9",
                    "4cc8f898-d0a5-498a-9264-2feeaa5879a9",
                    "4cc8f898-d0a5-598a-9264-2feeaa5879a9",
                    "4CC8F898-D0A5-598A-9264-2FEEAA5879A9",
                    "4CC8f898-d0a5-198a-9264-2feeaa5879a9"
    })
    void validUuid(String uuid) {
        Optional<String> validationResult = validator.validate(uuid);
        validationResult.ifPresent(Assertions::fail);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
                    "4cc8f898-d0a5-698a-9264-2feeaa5879a9",
                    "4cc8f898-d0a5-398a-9264",
    })
    void shouldReturnError(String uuid) {
        Optional<String> result = validator.validate(uuid);
        assertEquals(getFormatErrorMsg(uuid), result.get());
    }

}
