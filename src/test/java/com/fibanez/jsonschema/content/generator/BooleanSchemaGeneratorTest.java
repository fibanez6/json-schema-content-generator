package com.fibanez.jsonschema.content.generator;

import org.everit.json.schema.BooleanSchema;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BooleanSchemaGeneratorTest {

    private final BooleanSchemaGenerator generator = new BooleanSchemaGenerator();

    @Test
    void shouldThrowException_whenNullSchema() {
        assertThrows(NullPointerException.class, () -> generator.generate(null, JsonNode.ROOT));
    }

    @Test
    void shouldReturnRandomBoolean() {
        BooleanSchema schema = BooleanSchema.builder().build();
        Boolean result = generator.generate(schema, JsonNode.ROOT);
        assertThat(result).isNotNull();
    }

}