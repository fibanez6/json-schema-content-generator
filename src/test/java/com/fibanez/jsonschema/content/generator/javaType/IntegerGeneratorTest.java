package com.fibanez.jsonschema.content.generator.javaType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.createContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertThat(result, is(notNullValue()));
    }

    @ParameterizedTest
    @CsvSource({"-100,0","0,0", "0,5", "5,10", "10,10"})
    void shouldReturnValidString_byRange(int min, int max) {
        Integer result = generator.get(min, max);
        if (min == max) {
            assertThat(result, is(min));
        } else {
            assertThat(result, is(greaterThanOrEqualTo(min)));
            assertThat(result, is(lessThan(max)));
        }
    }

    @ParameterizedTest
    @CsvSource({"-20,-2,2", "1,10,1", "2,20,2", "7,70,7", "-5,5,5"})
    void shouldReturnValidNumber_multipleOf(int min, int max, int multipleOf) {
        Integer result = generator.get(min, max, multipleOf);
        assertThat(result, is(lessThan(max)));
        assertThat(result, is(greaterThanOrEqualTo(min)));
        assertThat(result % multipleOf, is(0));
    }

    @ParameterizedTest
    @CsvSource({"-2,2,3", "1,1,2", "1,4,5"})
    void shouldThrowException_multipleOf_noFound(int min, int max, int multipleOf) {
        assertThrows(IllegalArgumentException.class, () -> generator.get(min,max,multipleOf));
    }
}