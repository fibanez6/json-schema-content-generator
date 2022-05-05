package com.fibanez.jsonschema.content.generator.javaType;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.createContext;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegerGeneratorTest {

    private IntegerGenerator generator = new IntegerGenerator();

    @Test
    void shouldThrowException_minIsHigherMax() {
        int min = 0;
        int max = 10;
        assertThrows(IllegalArgumentException.class, () -> generator.get(max, min, 1));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void shouldThrowException_multipleOfNotPositive(int multipleOf) {
        assertThrows(IllegalArgumentException.class, () -> generator.get(0, 10, multipleOf));
    }

    @Test
    void shouldReturnValidInteger_default() {
        createContext();
        Integer result = generator.get();
        assertThat(result).isNotNull();
    }

    @ParameterizedTest
    @CsvSource({"-100,0","0,0", "0,5", "5,10", "10,10"})
    void shouldReturnValidString_byRange(int min, int max) {
        Integer result = generator.get(min, max);
        if (min == max) {
            assertThat(result).isEqualTo(min);
        } else {
            assertThat(result).isAtLeast(min);
            assertThat(result).isLessThan(max);
        }
    }

    @ParameterizedTest
    @CsvSource({"-20,-2,2", "1,10,1", "2,20,2", "7,70,7", "-5,5,5"})
    void shouldReturnValidNumber_multipleOf(int min, int max, int multipleOf) {
        Integer result = generator.get(min, max, multipleOf);
        assertThat(result).isAtLeast(min);
        assertThat(result).isLessThan(max);
        assertThat(result % multipleOf).isEqualTo(0);
    }

    @ParameterizedTest
    @CsvSource({"-2,2,3", "1,1,2", "1,4,5"})
    void shouldThrowException_multipleOf_noFound(int min, int max, int multipleOf) {
        assertThrows(IllegalArgumentException.class, () -> generator.get(min, max, multipleOf));
    }

    @ParameterizedTest
    @MethodSource("provideNoMultipleOfCases")
    void shouldReturnValidNumber_multipleOf_negated(IntegerGeneratorData data) {
        Integer result = generator.get(data.min, data.max, data.multipleOf, data.notMultipleOf);

        assertThat(result).isAtLeast(data.min);
        assertThat(result).isLessThan(data.max);
        assertThat(result % data.multipleOf).isEqualTo(0);
        assertTrue(data.notMultipleOf.stream().mapToInt(Number::intValue).noneMatch(n -> result % n == 0));
    }

    static Stream<Arguments> provideNoMultipleOfCases() {
        return Stream.of(
                Arguments.of(new IntegerGeneratorData(1, 10, 1, Collections.emptySet())),
                Arguments.of(new IntegerGeneratorData(1, 10, 1, Set.of(2))),
                Arguments.of(new IntegerGeneratorData(1, 10, 2, Set.of(3, 5))),
                Arguments.of(new IntegerGeneratorData(10, 15, 5, Set.of(3))),
                Arguments.of(new IntegerGeneratorData(10, 30, 2, Set.of(3, 5))),
                Arguments.of(new IntegerGeneratorData(10, 30, 2, Set.of(3, 5, 7)))
        );
    }

    @Test
    void shouldThrowException_multipleOf_negated_noFound() {
        int min = 10;
        int max = 15;
        int multipleOf = 5;
        Set<Number> notMultipleOf = Set.of(3, 5);
        assertThrows(GeneratorException.class, () -> generator.get(min, max, multipleOf, notMultipleOf));
    }

    @AllArgsConstructor
    private static class IntegerGeneratorData {
        private Integer min;
        private Integer max;
        private Integer multipleOf;
        private Set<Number> notMultipleOf;
    }
}