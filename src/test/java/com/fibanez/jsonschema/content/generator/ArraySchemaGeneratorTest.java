package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.JsonGeneratorConfig;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import org.everit.json.schema.ArraySchema;
import org.everit.json.schema.BooleanSchema;
import org.everit.json.schema.EnumSchema;
import org.everit.json.schema.NumberSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.createContext;
import static com.fibanez.jsonschema.content.testUtil.TestUtils.createJsonNode;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArraySchemaGeneratorTest {

    private final ArraySchemaGenerator generator = new ArraySchemaGenerator();

    @BeforeEach
    void setUp() {
        createContext();
    }

    @Test
    void shouldThrowException_whenNulls() {
        assertThrows(NullPointerException.class, () -> generator.generate(null, JsonNode.ROOT));
        assertThrows(NullPointerException.class, () -> generator.generate(ArraySchema.builder().build(), null));
    }

    @Test
    void shouldReturnEmptyArray_whenNoSchemas() {
        ArraySchema arraySchema = ArraySchema.builder().build();
        JSONArray result = generator.generate(arraySchema, JsonNode.ROOT);
        assertThat(result).isEmpty();
    }

    @ParameterizedTest(name = "#{index} - Generate array of: {0}")
    @MethodSource("provideArrayOfSchemas")
    void shouldReturnRandomArray_of(Class<?> clazz, Schema schema) {
        ArraySchema arraySchema = ArraySchema.builder().addItemSchema(schema).build();
        JSONArray result = generator.generate(arraySchema, JsonNode.ROOT);

        assertThat(result).isNotEmpty();
        assertArrayElement(result, clazz);
    }

    private static Stream<Arguments> provideArrayOfSchemas() {
        return Stream.of(
                Arguments.of(String.class, new StringSchema()),
                Arguments.of(Number.class, new NumberSchema()),
                Arguments.of(Boolean.class, new BooleanSchema(BooleanSchema.builder()))
        );
    }

    @ParameterizedTest
    @CsvSource(value = {"2,null", "null,10", "2,10", "5,5"}, nullValues = {"null"})
    void shouldReturnCorrectSize_withMinMaxSize(Integer min, Integer max) {
        ArraySchema arraySchema = ArraySchema.builder()
                .addItemSchema(new StringSchema())
                .minItems(min)
                .maxItems(max)
                .build();

        Integer expectedMin = Objects.nonNull(min) ? min : Context.DEFAULT_ARRAY_ITEMS_MIN;
        Integer expectedMax = Objects.nonNull(max) ? max : Context.DEFAULT_ARRAY_ITEMS_MAX;

        JSONArray result = generator.generate(arraySchema, JsonNode.ROOT);
        if (expectedMin.equals(expectedMax)) {
            assertThat(result).hasSize(expectedMax);
        } else {
            assertThat(result.length()).isAtLeast(expectedMin);
            assertThat(result.length()).isLessThan(expectedMax);
        }
    }

    @Test
    void shouldThrowException_whenMinItemsIsNegative() {
        ArraySchema arraySchema = ArraySchema.builder().minItems(-10).build();
        assertThrows(IllegalArgumentException.class, () -> generator.generate(arraySchema, JsonNode.ROOT));
    }

    @Test
    void shouldReturnArray_withUniqueItems() {
        Set<Object> possibleValues = Set.of("enum01", "enum02");
        EnumSchema schema = EnumSchema.builder().possibleValues(possibleValues).build();
        ArraySchema arraySchema = ArraySchema.builder()
                .addItemSchema(schema)
                .maxItems(10)
                .uniqueItems(true)
                .build();

        JSONArray result = generator.generate(arraySchema, JsonNode.ROOT);
        assertThat(result).isNotEmpty();
        assertThat(result).containsNoDuplicates();
    }

    @Test
    void shouldReturnPredefined_whenPath() throws Exception {
        Map<String, Supplier<String>> pathPredefinedValues = new HashMap<>();
        pathPredefinedValues.put("$.person[0]", () -> "Alice");
        pathPredefinedValues.put("$.person[1]", () -> "Asuna");
        pathPredefinedValues.put("$.person[2]", () -> "Kirito");
        pathPredefinedValues.put("$.person[3]", () -> "Shinon");
        pathPredefinedValues.put("$.person[4]", () -> "Yuuki");

        createContext(JsonGeneratorConfig.builder()
                .predefinedValueGenerators(pathPredefinedValues)
                .arrayItemsMin(5)
                .arrayItemsMax(5)
                .build());

        JsonNode jsonNode = createJsonNode("$.person", "person");
        ArraySchema arraySchema = ArraySchema.builder()
                .addItemSchema(new StringSchema())
                .build();

        JSONArray result = generator.generate(arraySchema, jsonNode);
        assertThat(result).containsExactlyElementsIn(Arrays.asList("Alice", "Asuna", "Kirito", "Shinon", "Yuuki"));
    }

    @Test
    void shouldReturnPredefined_whenPropertyName() throws Exception {
        List<Object> values = List.of("Stark", "Thor");
        createContext(JsonGeneratorConfig.builder()
                .predefinedValueGenerator("name", () -> RandomUtils.nextElement(values))
                .build());

        ArraySchema arraySchema = ArraySchema.builder().addItemSchema(new StringSchema()).build();
        JsonNode jsonNode = createJsonNode("$.person.name", "name");

        JSONArray result = generator.generate(arraySchema, jsonNode);
        assertThat(result).containsAnyIn(values);
    }

    private void assertArrayElement(JSONArray result, Class<?> clazz) {
        for (Object jsonElement : result) {
            assertInstanceOf(clazz, jsonElement);
        }
    }

}