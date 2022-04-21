package com.fibanez.jsonschema.content.generator.util;

import org.exparity.hamcrest.date.LocalDateMatchers;
import org.exparity.hamcrest.date.LocalDateTimeMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.exparity.hamcrest.date.LocalTimeMatchers.within;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RandomUtilsTest {

    @Test
    @DisplayName("Random between localDateTimes cannot be null")
    void shouldThrowException_betweenLocalDateTime_whenNulls() {
        assertThrows(NullPointerException.class, () -> RandomUtils.between(null, LocalDateTime.now()));
        assertThrows(NullPointerException.class, () -> RandomUtils.between(LocalDateTime.now(), null));
    }

    @Test
    @DisplayName("Random between localDateTimes cannot be From < To")
    void shouldThrowException_betweenLocalDateTime_FromIsAfterTo() {
        LocalDateTime tenDays = LocalDateTime.now().plusDays(10);
        LocalDateTime now = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> RandomUtils.between(tenDays, now));
    }

    @RepeatedTest(5)
    @DisplayName("Random between localDateTimes")
    void shouldReturnValid_betweenLocalDateTimes() {
        LocalDateTime from = LocalDateTime.of(1800,1,1, 9, 0);
        LocalDateTime to = LocalDateTime.of(1900,1,1, 9, 0);
        long period = from.until(to, ChronoUnit.DAYS);

        LocalDateTime result = RandomUtils.between(from, to);
        assertThat(result, LocalDateTimeMatchers.within(period, ChronoUnit.DAYS, from));
    }

    @Test
    @DisplayName("Random between localDates cannot be null")
    void shouldThrowException_betweenLocalDates_whenNulls() {
        assertThrows(NullPointerException.class, () -> RandomUtils.between(null, LocalDate.now()));
        assertThrows(NullPointerException.class, () -> RandomUtils.between(LocalDate.now(), null));
    }

    @Test
    @DisplayName("Random between localDates cannot be To < From")
    void shouldThrowException_betweenLocalDates_FromIsAfterTo() {
        LocalDate from = LocalDate.of(1800,1,1);
        LocalDate to = LocalDate.of(1900, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> RandomUtils.between(to, from));
    }

    @RepeatedTest(5)
    @DisplayName("Random between localDates")
    void shouldReturnValid_betweenLocalDates() {
        LocalDate from = LocalDate.of(1800, 1, 1);
        LocalDate to = LocalDate.of(1900,1,1);
        long period = from.datesUntil(to).count();

        LocalDate result = RandomUtils.between(from, to);
        assertThat(result, LocalDateMatchers.within(period, ChronoUnit.DAYS, from));
    }

    @Test
    @DisplayName("Random between localTimes cannot be null")
    void shouldThrowException_betweenLocalTimes_whenNulls() {
        assertThrows(NullPointerException.class, () -> RandomUtils.between(null, LocalTime.now()));
        assertThrows(NullPointerException.class, () -> RandomUtils.between(LocalTime.now(), null));
    }

    @Test
    @DisplayName("Random between localTimes cannot be To < From")
    void shouldThrowException_betweenLocalTimes_FromIsAfterTo() {
        LocalTime from = LocalTime.of(10, 0, 0);
        LocalTime to = LocalTime.of(12,0,0 );
        assertThrows(IllegalArgumentException.class, () -> RandomUtils.between(to, from));
    }

    @RepeatedTest(5)
    @DisplayName("Random between localTimes")
    void shouldReturnValid_localTime() {
        LocalTime from = LocalTime.of(10, 0, 0);
        LocalTime to = LocalTime.of(12,0,0 );
        long period = from.until(to, ChronoUnit.HOURS);

        LocalTime result = RandomUtils.between(from, to);
        assertThat(result, within(period, ChronoUnit.HOURS, to));
    }

    @Test
    @DisplayName("Random between durations cannot be null")
    void shouldThrowException_betweenDurations_whenNulls() {
        assertThrows(NullPointerException.class, () -> RandomUtils.between(null, Duration.ZERO));
        assertThrows(NullPointerException.class, () -> RandomUtils.between(Duration.ZERO, null));
    }

    @RepeatedTest(5)
    @DisplayName("Random between durations")
    void shouldReturnValid_betweenDurations() {
        Duration from = Duration.ZERO;
        Duration to = Duration.ofDays(1);

        Duration result = RandomUtils.between(from, to);
        assertThat(result, is(greaterThanOrEqualTo(from)));
        assertThat(result, is(lessThan(to)));
    }

    @Test
    @DisplayName("Random between durations cannot be To < From")
    void shouldThrowException_betweenDurations_FromIsAfterTo() {
        Duration from = Duration.ZERO;
        Duration to = Duration.ofDays(1);
        assertThrows(IllegalArgumentException.class, () -> RandomUtils.between(to, from));
    }

    @ParameterizedTest
    @DisplayName("Random between longs")
    @CsvSource({"-10,-1", "0,0", "1,10", "10,10", "-10,10"})
    void shouldReturnValid_betweenLong(long fromInclusive, long toExclusive) {
        long result = RandomUtils.between(fromInclusive, toExclusive);
        if (fromInclusive == toExclusive) {
            assertThat(result, is(fromInclusive));
        } else {
            assertThat(result, is(greaterThanOrEqualTo(fromInclusive)));
            assertThat(result, is(lessThan(toExclusive)));
        }
    }

    @Test
    @DisplayName("No random between long when origin=bond")
    void shouldReturnValid_betweenLong_whenOriginEqualsBound() {
        long result = RandomUtils.between(1L, 1L);
        assertThat(result, is(1L));
    }

    @Test
    @DisplayName("Random between long cannot be To < From")
    void shouldThrowException_betweenLongs_FromIsAfterTo() {
        long from = 0L;
        long to = 100L;
        assertThrows(IllegalArgumentException.class, () -> RandomUtils.between(to, from));
    }

    @ParameterizedTest
    @DisplayName("Random between ints")
    @CsvSource({"-10,-1", "0,0", "1,10", "10,10", "-10,10"})
    void shouldReturnValid_betweenInt(int fromInclusive, int toExclusive) {
        int result = RandomUtils.between(fromInclusive, toExclusive);
        if (fromInclusive == toExclusive) {
            assertThat(result, is(fromInclusive));
        } else {
            assertThat(result, is(greaterThanOrEqualTo(fromInclusive)));
            assertThat(result, is(lessThan(toExclusive)));
        }
    }

    @Test
    @DisplayName("No random between int when origin=bond")
    void shouldReturnValid_betweenInt_whenOriginEqualsBound() {
        int result = RandomUtils.between(1, 1);
        assertThat(result, is(1));
    }

    @Test
    @DisplayName("Random between int cannot be To < From")
    void shouldThrowException_betweenInts_FromIsAfterTo() {
        int from = 0;
        int to = 100;
        assertThrows(IllegalArgumentException.class, () -> RandomUtils.between(to, from));
    }

    @ParameterizedTest
    @DisplayName("Random next ints")
    @ValueSource(ints = {-10, 0, 1, 5, 15, Integer.MAX_VALUE})
    void shouldReturnValid_nextInt(int bound) {
        int result = RandomUtils.nextInt(bound);
        if (bound == 0) {
            assertThat(result, is(0));
        } else if (bound < 0){
            assertThat(result, is(greaterThanOrEqualTo(bound)));
        } else {
            assertThat(result, is(lessThan(bound)));
        }
    }

    @ParameterizedTest
    @DisplayName("Random between string lengths")
    @CsvSource({"0,0", "1,10", "10,10"})
    void shouldReturnValid_stringLength(int minLengthInclusive, int maxLengthExclusive) {
        String result = RandomUtils.string(minLengthInclusive, maxLengthExclusive);
        if (minLengthInclusive == maxLengthExclusive) {
            assertThat(result.length(), is(minLengthInclusive));
        } else {
            assertThat(result.length(), is(greaterThanOrEqualTo(minLengthInclusive)));
            assertThat(result.length(), is(lessThan(maxLengthExclusive)));
        }
    }

    @Test
    @DisplayName("No random between string lengths with negative values")
    void shouldThrowException_stringLength_whenNegatives() {
        assertThrows(IllegalArgumentException.class, () -> RandomUtils.string(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> RandomUtils.string(10, 1));
    }
}