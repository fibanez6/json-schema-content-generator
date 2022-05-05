package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.JsonGeneratorConfig;
import org.everit.json.schema.BooleanSchema;
import org.everit.json.schema.NumberSchema;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;
import org.everit.json.schema.internal.DateFormatValidator;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.FIXTURE;
import static com.fibanez.jsonschema.content.testUtil.TestUtils.createContext;
import static com.fibanez.jsonschema.content.testUtil.TestUtils.createJsonNode;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectSchemaGeneratorTest {

    private final ObjectSchemaGenerator generator = new ObjectSchemaGenerator();

    @BeforeEach
    void setUp() {
        createContext();
    }

    @Test
    void shouldThrowException_whenNulls() {
        assertThrows(NullPointerException.class, () -> generator.generate(null, JsonNode.ROOT));
        assertThrows(NullPointerException.class, () -> generator.generate(ObjectSchema.builder().build(), null));
    }

    @Test
    void shouldReturnEmptyObject_whenNoSchemas() {
        ObjectSchema objectSchema = ObjectSchema.builder().build();
        JSONObject result = generator.generate(objectSchema, JsonNode.ROOT);
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest(name = "#{index} - Generate object of: {0}")
    @MethodSource("provideObjectOfSchemas")
    void shouldReturnRandomObject_of(Class<?> clazz, Schema schema) {
        String propertyName = FIXTURE.create(String.class);
        ObjectSchema objectSchema = ObjectSchema.builder().addPropertySchema(propertyName, schema).build();

        JSONObject result = generator.generate(objectSchema, JsonNode.ROOT);
        assertThat(result.get(propertyName)).isInstanceOf(clazz);
    }

    private static Stream<Arguments> provideObjectOfSchemas() {
        return Stream.of(
                Arguments.of(String.class, new StringSchema()),
                Arguments.of(Number.class, new NumberSchema()),
                Arguments.of(Boolean.class, new BooleanSchema(BooleanSchema.builder()))
        );
    }

    @Test
    void shouldReturnRandomObject_fromRequiredObjects() {
        String requiredProperty01 = FIXTURE.create(String.class);
        String requiredProperty02 = FIXTURE.create(String.class);
        String property01 = FIXTURE.create(String.class);
        String property02 = FIXTURE.create(String.class);
        ObjectSchema objectSchema = ObjectSchema.builder()
                .addPropertySchema(requiredProperty01, new StringSchema())
                .addPropertySchema(requiredProperty02, new StringSchema())
                .addPropertySchema(property01, new StringSchema())
                .addPropertySchema(property02, new StringSchema())
                .addRequiredProperty(requiredProperty01)
                .addRequiredProperty(requiredProperty02)
                .build();

        createContext(JsonGeneratorConfig.builder().onlyRequiredProps().build());

        JSONObject result = generator.generate(objectSchema, JsonNode.ROOT);
        // required asserts
        assertTrue(result.has(requiredProperty01));
        assertTrue(result.has(requiredProperty02));
        // no required asserts
        assertFalse(result.has(property01));
        assertFalse(result.has(property02));
    }

    @Test
    void shouldReturnPredefined_whenPropertyName() throws Exception {
        String propertyName = "name";
        String propertyValue = "Stark";

        createContext(JsonGeneratorConfig.builder()
                .predefinedValueGenerator(propertyName, () -> propertyValue)
                .build());

        ObjectSchema objectSchema = ObjectSchema.builder()
                .addPropertySchema(propertyName, new StringSchema())
                .build();
        JsonNode jsonNode = createJsonNode("$.person.name", "name");
        JSONObject result = generator.generate(objectSchema, jsonNode);
        assertThat(result.get(propertyName)).isEqualTo(propertyValue);
    }

    @Test
    void shouldReturnPredefined_whenPath() {
        Map<String, Supplier<?>> pathPredefinedValues = new HashMap<>();
        pathPredefinedValues.put("$.person.name", () -> "Alice");
        pathPredefinedValues.put("$.person.familyName", () -> "Uchiha");
        pathPredefinedValues.put("$.person.age", () -> 16);
        pathPredefinedValues.put("$.person.dob", () -> LocalDate.of(2000, 1, 1));

        createContext(JsonGeneratorConfig.builder()
                .predefinedValueGenerators(pathPredefinedValues)
                .build());

        ObjectSchema personSchema = ObjectSchema.builder()
                .addPropertySchema("name", new StringSchema())
                .addPropertySchema("familyName", new StringSchema())
                .addPropertySchema("age", new NumberSchema())
                .addPropertySchema("dob", StringSchema.builder()
                        .formatValidator(new DateFormatValidator())
                        .build())
                .build();

        ObjectSchema objectSchema = ObjectSchema.builder()
                .addPropertySchema("person", personSchema)
                .build();

        JSONObject result = generator.generate(objectSchema, JsonNode.ROOT);
        JSONObject personObject = result.getJSONObject("person");
        assertThat(personObject.get("name")).isEqualTo("Alice");
        assertThat(personObject.get("familyName")).isEqualTo("Uchiha");
        assertThat(personObject.get("age")).isEqualTo(16);
        assertThat(personObject.get("dob").toString()).isEqualTo("2000-01-01");
    }

}