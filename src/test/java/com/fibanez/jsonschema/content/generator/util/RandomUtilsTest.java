package com.fibanez.jsonschema.content.generator.util;

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
import java.util.Collections;
import java.util.List;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.FIXTURE;
import static com.google.common.collect.Range.closedOpen;
import static com.google.common.truth.Truth.assertThat;
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

        LocalDateTime result = RandomUtils.between(from, to);
        assertThat(result).isIn(closedOpen(from, to));
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

        LocalDate result = RandomUtils.between(from, to);
        assertThat(result).isIn(closedOpen(from, to));
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

        LocalTime result = RandomUtils.between(from, to);
        assertThat(result).isIn(closedOpen(from, to));
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
        assertThat(result).isIn(closedOpen(from, to));
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
            assertThat(result).isEqualTo(fromInclusive);
        } else {
            assertThat(result).isIn(closedOpen(fromInclusive, toExclusive));
        }
    }

    @Test
    @DisplayName("No random between long when origin=bond")
    void shouldReturnValid_betweenLong_whenOriginEqualsBound() {
        long result = RandomUtils.between(1L, 1L);
        assertThat(result).isEqualTo(1L);
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
            assertThat(result).isEqualTo(fromInclusive);
        } else {
            assertThat(result).isIn(closedOpen(fromInclusive, toExclusive));
        }
    }

    @Test
    @DisplayName("No random between int when origin=bond")
    void shouldReturnValid_betweenInt_whenOriginEqualsBound() {
        int result = RandomUtils.between(1, 1);
        assertThat(result).isEqualTo(1);
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
            assertThat(result).isEqualTo(0);
        } else if (bound < 0){
            assertThat(result).isAtLeast(bound);
        } else {
            assertThat(result).isLessThan(bound);
        }
    }

    @ParameterizedTest
    @DisplayName("Random between string lengths")
    @CsvSource({"0,0", "1,10", "10,10"})
    void shouldReturnValid_stringLength(int minLengthInclusive, int maxLengthExclusive) {
        String result = RandomUtils.string(minLengthInclusive, maxLengthExclusive);
        if (minLengthInclusive == maxLengthExclusive) {
            assertThat(result).hasLength(minLengthInclusive);
        } else {
            assertThat(result.length()).isAtLeast(minLengthInclusive);
            assertThat(result.length()).isLessThan(maxLengthExclusive);
        }
    }

    @Test
    @DisplayName("No random between string lengths with negative values")
    void shouldThrowException_stringLength_whenNegatives() {
        assertThrows(IllegalArgumentException.class, () -> RandomUtils.string(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> RandomUtils.string(10, 1));
    }

    @Test
    @DisplayName("Random item from a list")
    void shouldReturnValid_nextElement() {
        List<String> collection = FIXTURE.collections().createCollection(List.class, String.class);
        String result = RandomUtils.nextElement(collection);
        assertThat(result).isIn(collection);
    }

    @Test
    @DisplayName("Random item from a list when empty")
    void shouldReturnNull_nextElement_whenEmpty() {
        List<String> collection = Collections.emptyList();
        String result = RandomUtils.nextElement(collection);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Random boolean")
    void shouldReturnValid_boolean() {
        Boolean result = RandomUtils.nextBoolean();
        assertThat(result).isInstanceOf(Boolean.class);
    }

}