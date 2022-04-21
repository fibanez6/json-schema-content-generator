package com.fibanez.jsonschema.content.generator.stringFormat;

import com.fibanez.jsonschema.content.Context;
import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.internal.DateTimeFormatValidator;
import org.exparity.hamcrest.date.LocalDateTimeMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.createContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateTimeFormatGeneratorTest {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private final FormatValidator formatValidator = new DateTimeFormatValidator();

    @Test
    void shouldThrowException_fromIsNull() {
        assertThrows(NullPointerException.class, () -> new DateTimeFormatGenerator(null, LocalDateTime.now()));
    }

    @Test
    void shouldThrowException_toIsNull() {
        assertThrows(NullPointerException.class, () -> new DateTimeFormatGenerator(LocalDateTime.now(), null));
    }

    @Test
    void shouldThrowException_fromIsAfterTo() {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(1);
        LocalDateTime toDate = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> new DateTimeFormatGenerator(toDate, fromDate));
    }

    @Test
    void shouldReturnValidString_default() {
        Context context = createContext();

        FormatGenerator generator = new DateTimeFormatGenerator(context);
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }

    @ParameterizedTest
    @CsvSource({
            "2022-01-01T10:00:00Z,2022-01-01T10:00:00Z", // same datetime
            "2022-01-01T10:00:00Z,2022-02-01T10:00:00Z", // a month
            "2022-01-01T11:11:11Z,2022-01-02T00:00:00Z", // origin time > bond time
    })
    void shouldReturnValidString_byRange(String from, String to) {
        createContext();

        LocalDateTime fromDate = LocalDateTime.parse(from, formatter);
        LocalDateTime toDate = LocalDateTime.parse(to, formatter);
        long period = fromDate.until(toDate, ChronoUnit.SECONDS);

        FormatGenerator generator = new DateTimeFormatGenerator(fromDate, toDate);
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);

        LocalDateTime resultDate = LocalDateTime.parse(result, formatter);
        assertThat(resultDate, LocalDateTimeMatchers.within(period, ChronoUnit.SECONDS, fromDate));
    }

    @ParameterizedTest
    @CsvSource({
            "yyyy/MM/dd'T'HH:mm:ss.SSS'Z'",
            "[dd]-[MM]-[yyyy]-[HH]-[mm]-[ss]",
            "yyyy_MM_dd'T'HH:mm:ss"
    })
    void shouldReturnValidString_customFormat(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime tenDaysAgo = LocalDateTime.now().minusDays(10);
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

        FormatGenerator generator = new DateTimeFormatGenerator(tenDaysAgo, oneDayAgo, formatter);
        String result = generator.get();
        LocalDateTime.parse(result, formatter);
    }

}