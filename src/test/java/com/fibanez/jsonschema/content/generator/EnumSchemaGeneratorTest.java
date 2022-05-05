package com.fibanez.jsonschema.content.generator;

import org.everit.json.schema.EnumSchema;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.FIXTURE;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnumSchemaGeneratorTest {

    private final EnumSchemaGenerator generator = new EnumSchemaGenerator();

    @Test
    void shouldThrowException_whenNullSchema() {
        assertThrows(NullPointerException.class, () -> generator.generate(null, JsonNode.ROOT));
    }

    @Test
    void shouldReturnRandomEnum() {
        Set<Object> possibleValues = FIXTURE.collections().createCollection(Set.class, String.class);

        EnumSchema schema = EnumSchema.builder().possibleValues(possibleValues).build();
        Object result = generator.generate(schema, JsonNode.ROOT);
        assertThat(result).isIn(possibleValues);
    }

    @Test
    void shouldReturnNull_whenNoPossibleValues() {
        EnumSchema schema = EnumSchema.builder().build();
        Object result = generator.generate(schema, JsonNode.ROOT);
        assertThat(result).isEqualTo(JSONObject.NULL);
    }
}