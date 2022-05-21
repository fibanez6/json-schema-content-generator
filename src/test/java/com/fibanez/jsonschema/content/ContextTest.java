package com.fibanez.jsonschema.content;

import com.fibanez.jsonschema.content.generator.ArraySchemaGenerator;
import com.fibanez.jsonschema.content.generator.BooleanSchemaGenerator;
import com.fibanez.jsonschema.content.generator.CombinedSchemaGenerator;
import com.fibanez.jsonschema.content.generator.ConstSchemaGenerator;
import com.fibanez.jsonschema.content.generator.EnumSchemaGenerator;
import com.fibanez.jsonschema.content.generator.Generator;
import com.fibanez.jsonschema.content.generator.JsonNode;
import com.fibanez.jsonschema.content.generator.NotSchemaGenerator;
import com.fibanez.jsonschema.content.generator.NullSchemaGenerator;
import com.fibanez.jsonschema.content.generator.NumberSchemaGenerator;
import com.fibanez.jsonschema.content.generator.ObjectSchemaGenerator;
import com.fibanez.jsonschema.content.generator.ReferenceSchemaGenerator;
import com.fibanez.jsonschema.content.generator.StringSchemaGenerator;
import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import com.fibanez.jsonschema.content.generator.javaType.IntegerGenerator;
import com.fibanez.jsonschema.content.generator.javaType.StringGenerator;
import com.fibanez.jsonschema.content.generator.stringFormat.DateFormatGenerator;
import com.fibanez.jsonschema.content.generator.stringFormat.DateTimeFormatGenerator;
import com.fibanez.jsonschema.content.generator.stringFormat.FormatGenerator.Format;
import com.fibanez.jsonschema.content.testUtil.TestSchema;
import org.everit.json.schema.ArraySchema;
import org.everit.json.schema.BooleanSchema;
import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.ConstSchema;
import org.everit.json.schema.EnumSchema;
import org.everit.json.schema.NotSchema;
import org.everit.json.schema.NullSchema;
import org.everit.json.schema.NumberSchema;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.ReferenceSchema;
import org.everit.json.schema.StringSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.FIXTURE;
import static com.fibanez.jsonschema.content.testUtil.TestUtils.createContext;
import static com.fibanez.jsonschema.content.testUtil.TestUtils.createJsonNode;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContextTest {

    private final JsonGeneratorConfig defaultConfig = JsonGeneratorConfig.builder().build();

    @Test
    void shouldReturnContext_withDefaults() {
        Context context = createContext(defaultConfig);

        assertThat(context.getDefinitionsPath()).isNull();
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

        assertThat(context.getStringLengthMax()).isAtLeast(0);
        assertThat(context.getStringLengthMin()).isLessThan(Integer.MAX_VALUE);

        assertThat(context.getNumberMin()).isEqualTo(0);
        assertThat(context.getNumberMax()).isEqualTo(Integer.MAX_VALUE);

        assertThat(context.getArrayItemsMin()).isEqualTo(1);
        assertThat(context.getArrayItemsMax()).isEqualTo(5);

        assertFalse(context.getSchemaGenerators().isEmpty());
        assertFalse(context.getStringFormatGenerators().isEmpty());
        assertFalse(context.getJavaTypeGenerators().isEmpty());
        assertFalse(context.getPredefinedValueGenerators().isEmpty());

        assertEquals(Context.current(), context);
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
                Arguments.of(ArraySchema.class, ArraySchemaGenerator.class),
                Arguments.of(BooleanSchema.class, BooleanSchemaGenerator.class),
                Arguments.of(CombinedSchema.class, CombinedSchemaGenerator.class),
                Arguments.of(ConstSchema.class, ConstSchemaGenerator.class),
                Arguments.of(EnumSchema.class, EnumSchemaGenerator.class),
                Arguments.of(NotSchema.class, NotSchemaGenerator.class),
                Arguments.of(NullSchema.class, NullSchemaGenerator.class),
                Arguments.of(NumberSchema.class, NumberSchemaGenerator.class),
                Arguments.of(ObjectSchema.class, ObjectSchemaGenerator.class),
                Arguments.of(ReferenceSchema.class, ReferenceSchemaGenerator.class),
                Arguments.of(StringSchema.class, StringSchemaGenerator.class)
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
        Map<String, Generator<String>> stringFormatGenerators = context.getStringFormatGenerators();
        assertInstanceOf(DateTimeFormatGenerator.class, stringFormatGenerators.get("date-time"));
        assertInstanceOf(Generator.class, stringFormatGenerators.get(rndKey));
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
        Map<Class<?>, Generator<?>> javaTypeGenerators = context.getJavaTypeGenerators();
        assertInstanceOf(StringGenerator.class, javaTypeGenerators.get(String.class));
        assertFalse(javaTypeGenerators.get(Integer.class) instanceof IntegerGenerator);
    }

    @Test
    void shouldThrowException_whenJavaTypeGeneratorNoFound() {
        createContext(defaultConfig);
        assertThrows(GeneratorException.class, () -> Context.getJavaTypeGenerator(this.getClass()));
    }

    @Test
    void shouldReturnContext_withPredefinedValueGenerators() {
        String property01 = FIXTURE.create(String.class);
        String property02 = FIXTURE.create(String.class);
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .predefinedValueGenerator(property01, () -> FIXTURE.create(String.class))
                .predefinedValueGenerator(property02, new StringGenerator())
                .build();
        Context context = createContext(config);
        Map<String, Supplier<?>> valueGenerators = context.getPredefinedValueGenerators();
        assertInstanceOf(Supplier.class, valueGenerators.get(property01));
        assertInstanceOf(StringGenerator.class, valueGenerators.get(property02));
        assertInstanceOf(Supplier.class, valueGenerators.get("name"));
    }

    @Test
    void shouldReturnGenerator_whenPredefinedValueGenerator_pathFound() throws Exception {
        String propertyName = FIXTURE.create(String.class);
        String path = FIXTURE.create(String.class) + "/" + propertyName;
        String pathValue = FIXTURE.create(String.class);
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .predefinedValueGenerator(path, () -> pathValue)
                .predefinedValueGenerator(propertyName, () -> FIXTURE.create(String.class))
                .build();
        createContext(config);
        JsonNode jsonNode = createJsonNode(path, propertyName);
        Optional<Supplier<?>> generator = Context.getPredefinedValueGenerator(jsonNode);
        assertEquals(pathValue, generator.get().get());
    }

    @Test
    void shouldReturnGenerator_whenPredefinedValueGenerator_propertyNameFound() throws Exception {
        String propertyName = FIXTURE.create(String.class);
        String propertyNameValue = FIXTURE.create(String.class);
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .predefinedValueGenerator(propertyName, () -> propertyNameValue)
                .build();
        createContext(config);
        JsonNode jsonNode = createJsonNode(FIXTURE.create(String.class), propertyName);
        Optional<Supplier<?>> generator = Context.getPredefinedValueGenerator(jsonNode);
        assertEquals(propertyNameValue, generator.get().get());
    }

    @Test
    void shouldReturnEmpty_whenPredefinedValueGenerator_noFound() {
        createContext(defaultConfig);
        Optional<Supplier<?>> generator = Context.getPredefinedValueGenerator(JsonNode.ROOT);
        assertTrue(generator.isEmpty());
    }

    @ParameterizedTest
    @CsvSource(value = {"null,null", "null,2000", "2000,null", "2000,2100"}, nullValues = {"null"})
    void shouldReturnContext_whenLocalDateFromTo(Integer yearFrom, Integer yearTo) {
        LocalDate from = Objects.nonNull(yearFrom) ? LocalDate.of(yearFrom, 1, 1) : null;
        LocalDate to = Objects.nonNull(yearTo) ? LocalDate.of(yearTo, 12, 31) : null;

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .dateFrom(from)
                .dateTo(to)
                .build();

        LocalDate expectedFrom = Objects.nonNull(yearFrom) ? from : Context.DEFAULT_LOCAL_DATE_FROM;
        LocalDate expectedTo = Objects.nonNull(yearTo) ? to : Context.DEFAULT_LOCAL_DATE_TO;

        Context context = createContext(config);
        assertEquals(expectedFrom, context.getDateFrom());
        assertEquals(expectedTo, context.getDateTo());
    }

    @ParameterizedTest
    @CsvSource(value = {"null,1000", "4000,null", "2000,1900"}, nullValues = {"null"})
    void shouldThrowException_whenInvalidLocalDateFromTo(Integer yearFrom, Integer yearTo) {
        LocalDate from = Objects.nonNull(yearFrom) ? LocalDate.of(yearFrom, 1, 1) : null;
        LocalDate to = Objects.nonNull(yearTo) ? LocalDate.of(yearTo, 12, 31) : null;

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .dateFrom(from)
                .dateTo(to)
                .build();
        assertThrows(IllegalArgumentException.class, () -> createContext(config));
    }

    @ParameterizedTest
    @CsvSource(value = {"null,null", "null,10", "22,null", "1,5"}, nullValues = {"null"})
    void shouldReturnContext_whenLocalTimeFromTo(Integer minHour, Integer maxHour) {
        LocalTime from = Objects.nonNull(minHour) ? LocalTime.of(minHour, 0, 0) : null;
        LocalTime to = Objects.nonNull(maxHour) ? LocalTime.of(maxHour, 0, 0) : null;
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .timeFrom(from)
                .timeTo(to)
                .build();

        LocalTime expectedFrom = Objects.nonNull(minHour) ? from : Context.DEFAULT_LOCAL_TIME_FROM;
        LocalTime expectedTo = Objects.nonNull(maxHour) ? to : Context.DEFAULT_LOCAL_TIME_TO;

        Context context = createContext(config);
        assertEquals(expectedFrom, context.getTimeFrom());
        assertEquals(expectedTo, context.getTimeTo());
    }

    @ParameterizedTest
    @CsvSource(value = {"10,1"})
    void shouldThrowException_whenInvalidLocalTimeFromTo(Integer minHour, Integer maxHour) {
        LocalTime from = LocalTime.of(minHour, 0, 0);
        LocalTime to = LocalTime.of(maxHour, 0, 0);
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .timeFrom(from)
                .timeTo(to)
                .build();
        assertThrows(IllegalArgumentException.class, () -> createContext(config));
    }

    @ParameterizedTest
    @CsvSource(value = {"null,null", "null,2000", "2000,null", "2000,2100"}, nullValues = {"null"})
    void shouldReturnContext_whenLocalDateTimeFromTo(Integer yearFrom, Integer yearTo) {
        LocalDateTime from = Objects.nonNull(yearFrom)
                ? LocalDateTime.of(yearFrom, 1, 1, 1, 0, 0) : null;
        LocalDateTime to = Objects.nonNull(yearTo)
                ? LocalDateTime.of(yearTo, 12, 31, 23, 59, 59) : null;

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .dateTimeFrom(from)
                .dateTimeTo(to)
                .build();

        LocalDateTime expectedFrom = Objects.nonNull(yearFrom) ? from : Context.DEFAULT_LOCAL_DATE_TIME_FROM;
        LocalDateTime expectedTo = Objects.nonNull(yearTo) ? to : Context.DEFAULT_LOCAL_DATE_TIME_TO;

        Context context = createContext(config);
        assertEquals(expectedFrom, context.getDateTimeFrom());
        assertEquals(expectedTo, context.getDateTimeTo());
    }

    @ParameterizedTest
    @CsvSource(value = {"null,1000", "4000,null", "2000,1900"}, nullValues = {"null"})
    void shouldThrowException_whenInvalidLocalDateTimeFromTo(Integer yearFrom, Integer yearTo) {
        LocalDateTime from = Objects.nonNull(yearFrom)
                ? LocalDateTime.of(yearFrom, 1, 1, 1, 0, 0) : null;
        LocalDateTime to = Objects.nonNull(yearTo)
                ? LocalDateTime.of(yearTo, 12, 31, 23, 59, 59) : null;

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .dateTimeFrom(from)
                .dateTimeTo(to)
                .build();
        assertThrows(IllegalArgumentException.class, () -> createContext(config));
    }

    @ParameterizedTest
    @CsvSource(value = {"null,null", "null,10", "1,5", "-10,-1", "-10,10"}, nullValues = {"null"})
    void shouldReturnContext_whenDurationFromTo(Integer dayFrom, Integer dayTo) {
        Duration durationFrom = Objects.nonNull(dayFrom) ? Duration.ofDays(dayFrom) : null;
        Duration durationTo = Objects.nonNull(dayTo) ? Duration.ofDays(dayTo) : null;
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .durationFrom(durationFrom)
                .durationTo(durationTo)
                .build();

        Duration expectedFrom = Objects.nonNull(dayFrom) ? durationFrom : Context.DEFAULT_DURATION_FROM;
        Duration expectedTo = Objects.nonNull(dayTo) ? durationTo : Context.DEFAULT_DURATION_TO;

        Context context = createContext(config);
        assertEquals(expectedFrom, context.getDurationFrom());
        assertEquals(expectedTo, context.getDurationTo());
    }

    @ParameterizedTest
    @CsvSource(value = {"null,-5", "1000, null", "5,1"}, nullValues = {"null"})
    void shouldThrowException_whenInvalidDurationFromTo(Integer DayFrom, Integer Dayto) {
        Duration durationFrom = Objects.nonNull(DayFrom) ? Duration.ofDays(DayFrom) : null;
        Duration durationTo = Objects.nonNull(Dayto) ? Duration.ofDays(Dayto) : null;
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .durationFrom(durationFrom)
                .durationTo(durationTo)
                .build();
        assertThrows(IllegalArgumentException.class, () -> createContext(config));
    }

    @ParameterizedTest
    @CsvSource(value = {"null,null", "null,10", "1,5"}, nullValues = {"null"})
    void shouldReturnContext_whenStringLengthMaxMin(Integer min, Integer max) {
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .stringLengthMin(min)
                .stringLengthMax(max)
                .build();

        Integer expectedMin = Objects.nonNull(min) ? min : Context.DEFAULT_STRING_LENGTH_MIN;
        Integer expectedMax = Objects.nonNull(max) ? max : Context.DEFAULT_STRING_LENGTH_MAX;

        Context context = createContext(config);
        assertEquals(expectedMin, context.getStringLengthMin());
        assertEquals(expectedMax, context.getStringLengthMax());
    }

    @ParameterizedTest
    @CsvSource(value = {"null,1", "100,null", "10,1", "-5,5", "-5,-1"}, nullValues = {"null"})
    void shouldThrowException_whenInvalidStringLengthMaxMi(Integer min, Integer max) {
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .stringLengthMin(min)
                .stringLengthMax(max)
                .build();
        assertThrows(IllegalArgumentException.class, () -> createContext(config));
    }

    @ParameterizedTest
    @CsvSource(value = {"null,null", "null,10", "1,5", "-10,-1", "-10,10"}, nullValues = {"null"})
    void shouldReturnContext_whenNumberMaxMin(Integer min, Integer max) {
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .numberMin(min)
                .numberMax(max)
                .build();

        Integer expectedMin = Objects.nonNull(min) ? min : Context.DEFAULT_NUMBER_MIN;
        Integer expectedMax = Objects.nonNull(max) ? max : Context.DEFAULT_NUMBER_MAX;

        Context context = createContext(config);
        assertEquals(expectedMin, context.getNumberMin());
        assertEquals(expectedMax, context.getNumberMax());
    }

    @ParameterizedTest
    @CsvSource(value = {"null,-5", "5,1"}, nullValues = {"null"})
    void shouldThrowException_whenInvalidNumberMaxMin(Integer min, Integer max) {
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .numberMin(min)
                .numberMax(max)
                .build();
        assertThrows(IllegalArgumentException.class, () -> createContext(config));
    }

    @ParameterizedTest
    @CsvSource(value = {"null,null", "null,10", "1,5"}, nullValues = {"null"})
    void shouldReturnContext_whenArrayItemsMaxMin(Integer min, Integer max) {
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .arrayItemsMin(min)
                .arrayItemsMax(max)
                .build();

        Integer expectedMin = Objects.nonNull(min) ? min : Context.DEFAULT_ARRAY_ITEMS_MIN;
        Integer expectedMax = Objects.nonNull(max) ? max : Context.DEFAULT_ARRAY_ITEMS_MAX;

        Context context = createContext(config);
        assertEquals(expectedMin, context.getArrayItemsMin());
        assertEquals(expectedMax, context.getArrayItemsMax());
    }

    @ParameterizedTest
    @CsvSource(value = {"100, null", "null,-5", "5,1", "-5,0", "-10,-5"}, nullValues = {"null"})
    void shouldThrowException_whenInvalidArrayItemsMaxMin(Integer min, Integer max) {
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .arrayItemsMin(min)
                .arrayItemsMax(max)
                .build();
        assertThrows(IllegalArgumentException.class, () -> createContext(config));
    }

}