package com.fibanez.jsonschema.content;

import com.fibanez.jsonschema.content.generator.NumberSchemaGenerator;
import com.fibanez.jsonschema.content.generator.ObjectSchemaGenerator;
import com.fibanez.jsonschema.content.generator.StringSchemaGenerator;
import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import com.fibanez.jsonschema.content.generator.javaType.IntegerGenerator;
import com.fibanez.jsonschema.content.generator.javaType.StringGenerator;
import com.fibanez.jsonschema.content.generator.stringFormat.DateFormatGenerator;
import com.fibanez.jsonschema.content.generator.stringFormat.DateTimeFormatGenerator;
import com.fibanez.jsonschema.content.generator.stringFormat.FormatGenerator.Format;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import com.fibanez.jsonschema.content.testUtil.TestSchema;
import org.everit.json.schema.NumberSchema;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.StringSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.FIXTURE;
import static com.fibanez.jsonschema.content.testUtil.TestUtils.createContext;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContextTest {

    private final JsonGeneratorConfig defaultConfig = JsonGeneratorConfig.builder().build();

    @Test
    void shouldReturnContext_withDefaults() {
        Context context = createContext(defaultConfig);

        assertThat(context.getDefinitionsPath(), isEmptyOrNullString());
        assertFalse(context.isOnlyRequiredProps());

        assertNotNull(context.getDateFormatter());
        assertNotNull(context.getDateFrom());
        assertNotNull(context.getDateTo());

        assertNotNull(context.getTimeFormatter());
        assertNotNull(context.getTimeFrom());
        assertNotNull(context.getTimeTo());

        assertNotNull(context.getDateTimeFormatter());
        assertNotNull(context.getDateTimeFrom());
        assertNotNull(context.getDateTimeTo());

        assertNotNull(context.getDurationFrom());
        assertNotNull(context.getDurationTo());

        assertThat(context.getStringLengthMax(), is(greaterThanOrEqualTo(0)));
        assertThat(context.getStringLengthMin(), is(lessThanOrEqualTo(Integer.MAX_VALUE)));

        assertThat(context.getNumberMin(), is(0));
        assertThat(context.getNumberMax(), is(Integer.MAX_VALUE));

        assertThat(context.getArrayItemsMin(), is(1));
        assertThat(context.getArrayItemsMax(), is(5));

        assertFalse(context.getSchemaGenerators().isEmpty());
        assertFalse(context.getStringFormatGenerators().isEmpty());
        assertFalse(context.getJavaTypeGenerators().isEmpty());

        assertEquals(Context.context(), context);
    }

    @Test
    void shouldReturnContext_withDefinitionPath() {
        String path = FIXTURE.create(String.class);
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .definitionsPath(path)
                .build();

        Context context = createContext(config);
        assertEquals(path, context.getDefinitionsPath());
    }

    @Test
    void shouldReturnContext_withIsOnlyRequiredProps() {
        Context context = createContext(defaultConfig);
        assertFalse(context.isOnlyRequiredProps());
    }

    @Test
    void shouldReturnContext_withDateFormatter() {
        String pattern = "[dd][MM][yyyy]";
        LocalDate today = LocalDate.now();
        String expected = DateTimeFormatter.ofPattern(pattern).format(today);

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .dateFormatter(pattern)
                .build();
        Context context = createContext(config);
        String result = context.getDateFormatter().format(today);
        assertEquals(expected, result);
    }

    @Test
    void shouldReturnContext_withDateTimeFormatter() {
        String pattern = "[dd][MM][yyyy]'T'[HH][mm][ss]SSS'Z'";
        LocalDateTime today = LocalDateTime.now();
        String expected = DateTimeFormatter.ofPattern(pattern).format(today);

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .dateTimeFormatter(pattern)
                .build();
        Context context = createContext(config);
        String result = context.getDateTimeFormatter().format(today);
        assertEquals(expected, result);
    }

    @Test
    void shouldReturnContext_withTimeFormatter() {
        String pattern = "[HH]-[mm]-[ss]";
        LocalTime today = LocalTime.now();
        String expected = DateTimeFormatter.ofPattern(pattern).format(today);

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .timeFormatter(pattern)
                .build();
        Context context = createContext(config);
        String result = context.getTimeFormatter().format(today);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @MethodSource("provideSchemaGenerators")
    void shouldReturnContext_withSchemaGenerators(Class<?> schema, Class<?> generator) {
        Context context = createContext(defaultConfig);
        assertInstanceOf(generator, context.getSchemaGenerators().get(schema));
    }

    private static Stream<Arguments> provideSchemaGenerators() {
        return Stream.of(
                Arguments.of(ObjectSchema.class, ObjectSchemaGenerator.class),
                Arguments.of(StringSchema.class, StringSchemaGenerator.class),
                Arguments.of(NumberSchema.class, NumberSchemaGenerator.class)
        );
    }

    @Test
    void shouldThrowException_whenSchemaGeneratorNoFound() {
        createContext(defaultConfig);
        assertThrows(GeneratorException.class, () -> Context.getSchemaGenerator(TestSchema.class));
    }

    @Test
    void shouldReturnContext_withStringFormatGenerators() {
        String rndKey = FIXTURE.create(String.class);
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .stringFormatGenerator(Format.DATE.value(), () -> FIXTURE.create(String.class))
                .stringFormatGenerator(rndKey, () -> FIXTURE.create(String.class))
                .build();
        Context context = createContext(config);
        Map<String, Supplier<String>> stringFormatGenerators = context.getStringFormatGenerators();
        assertInstanceOf(DateTimeFormatGenerator.class, stringFormatGenerators.get("date-time"));
        assertInstanceOf(Supplier.class, stringFormatGenerators.get(rndKey));
        assertFalse(stringFormatGenerators.get("date") instanceof DateFormatGenerator);
    }

    @Test
    void shouldThrowException_whenFormatGeneratorNoFound() {
        createContext(defaultConfig);
        assertThrows(GeneratorException.class, () -> Context.getFormatGenerator(FIXTURE.create(String.class)));
    }

    @Test
    void shouldReturnContext_withJavaTypeGenerators() {
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .javaTypeGenerator(Integer.class, () -> FIXTURE.create(Integer.class))
                .build();
        Context context = createContext(config);
        Map<Class<?>, Supplier<?>> javaTypeGenerators = context.getJavaTypeGenerators();
        assertInstanceOf(StringGenerator.class, javaTypeGenerators.get(String.class));
        assertFalse(javaTypeGenerators.get(Integer.class) instanceof IntegerGenerator);
    }

    @Test
    void shouldThrowException_whenJavaTypeGeneratorNoFound() {
        createContext(defaultConfig);
        assertThrows(GeneratorException.class, () -> Context.getJavaTypeGenerator(this.getClass()));
    }

    @Test
    void shouldReturnContext_whenStringLengthMaxMin() {
        Integer min = RandomUtils.between(0,10);
        Integer max = min + RandomUtils.between(0,10);
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .stringLengthMin(min)
                .StringLengthMax(max)
                .build();
        Context context = createContext(config);
        assertEquals(min, context.getStringLengthMin());
        assertEquals(max, context.getStringLengthMax());
    }

    @Test
    void shouldReturnContext_whenLocalDateTimeFromTo() {
        LocalDateTime min = LocalDateTime.MIN;
        LocalDateTime max = LocalDateTime.MAX;
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .dateTimeFrom(min)
                .dateTimeTo(max)
                .build();
        Context context = createContext(config);
        assertEquals(min, context.getDateTimeFrom());
        assertEquals(max, context.getDateTimeTo());
    }

    @Test
    void shouldReturnContext_whenLocalDateFromTo() {
        LocalDate min = LocalDate.MIN;
        LocalDate max = LocalDate.MAX;
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .dateFrom(min)
                .dateTo(max)
                .build();
        Context context = createContext(config);
        assertEquals(min, context.getDateFrom());
        assertEquals(max, context.getDateTo());
    }

    @Test
    void shouldReturnContext_whenLocalTimeFromTo() {
        LocalTime min = LocalTime.MIN;
        LocalTime max = LocalTime.MAX;
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .timeFrom(min)
                .timeTo(max)
                .build();
        Context context = createContext(config);
        assertEquals(min, context.getTimeFrom());
        assertEquals(max, context.getTimeTo());
    }

    @Test
    void shouldReturnContext_whenDurationFromTo() {
        Duration min = Duration.ofDays(90);
        Duration max = Duration.ofDays(100);
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .durationFrom(min)
                .durationTo(max)
                .build();
        Context context = createContext(config);
        assertEquals(min, context.getDurationFrom());
        assertEquals(max, context.getDurationTo());
    }

    @Test
    void shouldReturnContext_whenNumberMaxMin() {
        Number min = 10;
        Number max = 100;
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .numberMin(min)
                .numberMax(max)
                .build();
        Context context = createContext(config);
        assertEquals(min, context.getNumberMin());
        assertEquals(max, context.getNumberMax());
    }

    @Test
    void shouldReturnContext_whenArrayItemsMaxMin() {
        int min = 10;
        int max = 100;
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .arrayItemsMin(min)
                .arrayItemsMax(max)
                .build();
        Context context = createContext(config);
        assertEquals(min, context.getArrayItemsMin());
        assertEquals(max, context.getArrayItemsMax());
    }

}