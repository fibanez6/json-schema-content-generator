package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import org.everit.json.schema.NumberSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Objects;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.createContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumberSchemaGeneratorTest {

    private final NumberSchemaGenerator generator = new NumberSchemaGenerator();
    private NumberSchema.Builder schemaBuilder;

    @BeforeEach
    void setUp() {
        schemaBuilder = NumberSchema.builder();
        createContext();
    }

    @Test
    void shouldThrowException_whenNulls() {
        assertThrows(NullPointerException.class, () -> generator.generate(null, CrumbPath.ROOT));
//        assertThrows(NullPointerException.class, () -> generator.generate(schemaBuilder.build(), null)); // TODO
    }

    @Test
    void shouldReturnRandomInteger_withTypeInteger() {
        NumberSchema schema = new NumberSchema(NumberSchema.builder());
        Number result = generator.generate(schema, CrumbPath.ROOT);
        assertInstanceOf(Integer.class, result);
    }

    @ParameterizedTest
    @CsvSource(value = {"2,null", "null,10", "2,10", "5,5", "-10,-2", "-10,10"}, nullValues={"null"})
    void shouldReturnRandomValue_withSchemaMinMax(Integer min, Integer max) {
        schemaBuilder = schemaBuilder.minimum(min).maximum(max);
        NumberSchema schema = new NumberSchema(schemaBuilder);

        Integer result = generator.generate(schema, CrumbPath.ROOT).intValue();

        Integer expectedMin = Objects.nonNull(min) ? min : Context.DEFAULT_NUMBER_MIN;
        Integer expectedMax = Objects.nonNull(max) ? max : Context.DEFAULT_NUMBER_MAX;
        assertThat(result, is(greaterThanOrEqualTo(expectedMin)));
        if (expectedMin.equals(expectedMax)) {
            assertThat(result, is(equalTo(expectedMax)));
        } else {
            assertThat(result, is(lessThan(expectedMax)));
        }
    }

    @ParameterizedTest
    @CsvSource(value = {"2,null", "null,10", "2,10", "-10,-2", "-10,10"}, nullValues={"null"})
    void shouldReturnRandomValue_withSchemaExclusiveMinMax(Integer min, Integer max) {
        schemaBuilder = schemaBuilder.exclusiveMinimum(min).exclusiveMaximum(max);
        NumberSchema schema = new NumberSchema(schemaBuilder);

        Integer result = generator.generate(schema, CrumbPath.ROOT).intValue();

        Integer expectedMin = Objects.nonNull(min) ? min : 0;
        Integer expectedMax = Objects.nonNull(max) ? max : Integer.MAX_VALUE;
        assertThat(result, is(greaterThanOrEqualTo(expectedMin)));
        assertThat(result, is(lessThan(expectedMax)));
    }

    @ParameterizedTest
    @CsvSource(value = {"0,10,null", "-20,-2,2", "1,10,1", "2,20,2", "7,70,7", "-5,5,5"}, nullValues={"null"})
    void shouldReturnRandomValue_withMultipleOf(int min, int max, Integer multipleOf) {
        schemaBuilder = schemaBuilder.minimum(min).maximum(max).multipleOf(multipleOf);
        NumberSchema schema = new NumberSchema(schemaBuilder);

        Integer result = generator.generate(schema, CrumbPath.ROOT).intValue();
        assertThat(result, is(greaterThanOrEqualTo(min)));
        assertThat(result, is(lessThan(max)));

        int expectedMultipleOf = Objects.nonNull(multipleOf) ? multipleOf : 1;
        assertTrue(result % expectedMultipleOf == 0);
    }

}