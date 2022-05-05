package com.fibanez.jsonschema.content.generator.stringFormat;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.testUtil.validator.TimeFormatValidator;
import org.everit.json.schema.FormatValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.createContext;
import static com.google.common.collect.Range.closedOpen;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeFormatGeneratorTest {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_TIME;
    private final FormatValidator formatValidator = new TimeFormatValidator();

    @Test
    void shouldThrowException_fromIsNull() {
        assertThrows(NullPointerException.class, () -> new TimeFormatGenerator(null, LocalTime.now()));
    }

    @Test
    void shouldThrowException_toIsNull() {
        assertThrows(NullPointerException.class, () -> new TimeFormatGenerator(LocalTime.now(), null));
    }

    @Test
    void shouldThrowException_fromIsAfterTo() {
        LocalTime from = LocalTime.of(10,0,0);
        LocalTime to = LocalTime.of(12,0,0);
        assertThrows(IllegalArgumentException.class, () -> new TimeFormatGenerator(to, from));
    }

    @Test
    void shouldReturnValidString_default() {
        Context context = createContext();

        FormatGenerator generator = new TimeFormatGenerator(context);
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }

    @ParameterizedTest
    @CsvSource({
            "10:00:00,10:00:00", // same time
            "10:00:00,11:00:00", // a hour
    })
    void shouldReturnValidString_byRange(String from, String to) {
        createContext();

        LocalTime fromDate = LocalTime.parse(from, formatter);
        LocalTime toDate = LocalTime.parse(to, formatter);

        FormatGenerator generator = new TimeFormatGenerator(fromDate, toDate);
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
        LocalTime resultDate = LocalTime.parse(result, formatter);
        if (from.equals(to)) {
            assertThat(resultDate).isEqualTo(fromDate);
        } else {
            assertThat(resultDate).isIn(closedOpen(fromDate, toDate));
        }
    }

    @ParameterizedTest
    @CsvSource({"HH-mm:-ss", "[HH]/[mm]/[ss]"})
    void shouldReturnValidString_customFormat(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalTime from = LocalTime.of(10, 0, 0);
        LocalTime to = LocalTime.of(11, 11, 11);

        FormatGenerator generator = new TimeFormatGenerator(from, to, formatter);
        String result = generator.get();
        LocalTime.parse(result, formatter);
    }

}