package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.testUtil.validator.TimeFormatValidator;
import com.fibanez.jsonschema.content.validator.DurationFormatValidator;
import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.StringSchema;
import org.everit.json.schema.internal.DateFormatValidator;
import org.everit.json.schema.internal.DateTimeFormatValidator;
import org.everit.json.schema.internal.EmailFormatValidator;
import org.everit.json.schema.internal.HostnameFormatValidator;
import org.everit.json.schema.internal.IPV4Validator;
import org.everit.json.schema.internal.IPV6Validator;
import org.everit.json.schema.internal.URIFormatValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.stream.Stream;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.createContext;
import static com.fibanez.jsonschema.content.testUtil.TestUtils.validate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringSchemaGeneratorTest {

    private final StringSchemaGenerator generator = new StringSchemaGenerator();
    private StringSchema.Builder schemaBuilder;

    @BeforeEach
    void setUp() {
        schemaBuilder = StringSchema.builder();
        createContext();
    }

    @Test
    void shouldThrowException_whenNulls() {
        assertThrows(NullPointerException.class, () -> generator.generate(null, CrumbPath.ROOT));
        assertThrows(NullPointerException.class, () -> generator.generate(schemaBuilder.build(), null));
    }

    @ParameterizedTest
    @CsvSource(value = {"1,null", "null,5" ,"5,5", "5,20"}, nullValues={"null"})
    void shouldReturnRandomValue_withSchemaMinMax(Integer min, Integer max) {
        schemaBuilder = schemaBuilder.minLength(min).maxLength(max);
        StringSchema schema = new StringSchema(schemaBuilder);

        String result = generator.generate(schema, CrumbPath.ROOT);

        Integer expectedMin = Objects.nonNull(min) ? min : Context.DEFAULT_STRING_LENGTH_MIN;
        Integer expectedMax = Objects.nonNull(max) ? max : Context.DEFAULT_STRING_LENGTH_MAX;
        assertThat(result.length(), is(greaterThanOrEqualTo(expectedMin)));
        if (expectedMin.equals(expectedMax)) {
            assertThat(result.length(), is(equalTo(expectedMax)));
        } else {
            assertThat(result.length(), is(lessThan(expectedMax)));
        }
    }

    @ParameterizedTest(name = "#{index} - Generate string from StringFormat: {0}")
    @MethodSource("provideFormatValidators")
    void shouldReturnRandomValue_withFormat(FormatValidator formatValidator) {
        schemaBuilder = schemaBuilder.formatValidator(formatValidator);
        StringSchema schema = new StringSchema(schemaBuilder);

        String result = generator.generate(schema, CrumbPath.ROOT);
        validate(formatValidator, result);
    }

    private static Stream<Arguments> provideFormatValidators() {
        return Stream.of(
                Arguments.of(new DateFormatValidator()),
                Arguments.of(new DateTimeFormatValidator()),
                Arguments.of(new DurationFormatValidator()),
                Arguments.of(new EmailFormatValidator()),
                Arguments.of(new HostnameFormatValidator()),
                Arguments.of(new IPV4Validator()),
                Arguments.of(new IPV6Validator()),
                Arguments.of(new TimeFormatValidator()),
                Arguments.of(new URIFormatValidator())
        );
    }
}