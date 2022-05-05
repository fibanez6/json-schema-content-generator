package com.fibanez.jsonschema.content.generator.javaType;

import com.fibanez.jsonschema.content.testUtil.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringGeneratorTest {

    private final StringGenerator generator = new StringGenerator();

    @Test
    void shouldThrowException_minLengthIsHigherMaxLength() {
        int min = 0;
        int max = 10;
        assertThrows(IllegalArgumentException.class, () -> generator.get(max, min));
    }

    @ParameterizedTest
    @CsvSource(value = {"0,null", "null,10"}, nullValues={"null"})
    void shouldThrowException_invalidMinLengthMaxLength(Integer min, Integer max) {
        assertThrows(NullPointerException.class, () -> generator.get(min, max));
    }

    @Test
    void shouldReturnValidString_default() {
        TestUtils.createContext();
        String result = generator.get();
        assertThat(result).isNotEmpty();
    }

    @ParameterizedTest
    @CsvSource({"0,0", "0,5", "5,10", "10,10"})
    void shouldReturnValidString_byRange(int min, int max) {
        String result = generator.get(min, max);
        if (min == max) {
            assertThat(result).hasLength(min);
        } else {
            assertThat(result.length()).isAtLeast(min);
            assertThat(result.length()).isLessThan(max);
        }
    }

}