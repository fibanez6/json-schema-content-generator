package com.fibanez.jsonschema.content.generator.stringFormat;

import com.fibanez.jsonschema.content.Context;
import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.internal.DateFormatValidator;
import org.exparity.hamcrest.date.LocalDateMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.createContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateFormatGeneratorTest {

    private final FormatValidator formatValidator = new DateFormatValidator();

    @Test
    void shouldThrowException_fromIsNull() {
        assertThrows(NullPointerException.class, () -> new DateFormatGenerator(null, LocalDate.now()));
    }

    @Test
    void shouldThrowException_toIsNull() {
        assertThrows(NullPointerException.class, () -> new DateFormatGenerator(LocalDate.now(), null));
    }

    @Test
    void shouldThrowException_fromIsAfterTo() {
        LocalDate fromDate = LocalDate.now().minusDays(1);
        LocalDate toDate = LocalDate.now();

        assertThrows(IllegalArgumentException.class, () -> new DateFormatGenerator(toDate, fromDate));
    }

    @Test
    void shouldReturnValidString_default() {
        Context context = createContext();

        FormatGenerator generator = new DateFormatGenerator(context);
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);
    }

    @ParameterizedTest
    @CsvSource({
            "2022-01-01,2022-01-01", // same date
            "2022-01-01,2022-01-02", // a month
            "2022-01-01,2023-01-01"  // a year after
    })
    void shouldReturnValidString_byRange(String from, String to) {
        createContext();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fromDate = LocalDate.parse(from, formatter);
        LocalDate toDate = LocalDate.parse(to, formatter);
        long period = fromDate.datesUntil(toDate).count();

        FormatGenerator generator = new DateFormatGenerator(fromDate, toDate);
        String result = generator.get();

        Optional<String> validationResult = formatValidator.validate(result);
        validationResult.ifPresent(Assertions::fail);

        LocalDate resultDate = LocalDate.parse(result, formatter);
        assertThat(resultDate, LocalDateMatchers.within(period, ChronoUnit.DAYS, fromDate));
    }

    @ParameterizedTest
    @CsvSource({"yyyy/MM/dd", "dd-MM-yyyy", "yyyy_MM_dd"})
    void shouldReturnValidString_customFormat(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
        LocalDate oneDayAgo = LocalDate.now().minusDays(1);

        FormatGenerator generator = new DateFormatGenerator(tenDaysAgo, oneDayAgo, formatter);
        String result = generator.get();
        LocalDate.parse(result, formatter);
    }

}