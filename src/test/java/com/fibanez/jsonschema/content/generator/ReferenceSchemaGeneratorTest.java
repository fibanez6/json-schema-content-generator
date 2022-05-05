package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.ReferenceSchema;
import org.everit.json.schema.StringSchema;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.FIXTURE;
import static com.fibanez.jsonschema.content.testUtil.TestUtils.createContext;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReferenceSchemaGeneratorTest {

    private final ReferenceSchemaGenerator generator = new ReferenceSchemaGenerator();

    @BeforeEach
    void setUp() {
        createContext();
    }

    @Test
    void shouldThrowException_whenNulls() {
        assertThrows(NullPointerException.class, () -> generator.generate(null, JsonNode.ROOT));
        assertThrows(NullPointerException.class, () -> generator.generate(ReferenceSchema.builder().build(), null));
    }

    @Test
    void shouldThrowException_whenNoReferredSchema() {
        ReferenceSchema schema = ReferenceSchema.builder().build();
        assertThrows(GeneratorException.class, () -> generator.generate(schema, JsonNode.ROOT));
    }

    @Test
    void shouldReturnValidObject_whenDefinedAsStringSchema() {
        String refValue = FIXTURE.create(String.class);
        ReferenceSchema schema = ReferenceSchema.builder().refValue(refValue).build();
        schema.setReferredSchema(StringSchema.builder().build());

        String result = (String) generator.generate(schema, JsonNode.ROOT);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldReturnValidObject_whenDefinedAsObjectSchema() {
        String refValue = FIXTURE.create(String.class);
        ReferenceSchema schema = ReferenceSchema.builder().refValue(refValue).build();
        ObjectSchema.Builder objectSchemaBuilder = ObjectSchema.builder()
                .addPropertySchema("user", new StringSchema(StringSchema.builder()))
                .addPropertySchema("company", new StringSchema(StringSchema.builder()));

        schema.setReferredSchema(new ObjectSchema(objectSchemaBuilder));

        JSONObject result = (JSONObject) generator.generate(schema, JsonNode.ROOT);
        assertThat(result).isNotNull();
        assertThat(result.getString("user")).isNotEmpty();
        assertThat(result.getString("company")).isNotEmpty();
    }
}