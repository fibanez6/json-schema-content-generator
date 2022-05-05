package com.fibanez.jsonschema.content.generator;

import org.everit.json.schema.ConstSchema;
import org.junit.jupiter.api.Test;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.FIXTURE;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConstantSchemaGeneratorTest {

    private final ConstantSchemaGenerator generator = new ConstantSchemaGenerator();

    @Test
    void shouldThrowException_whenNullSchema() {
        assertThrows(NullPointerException.class, () -> generator.generate(null, JsonNode.ROOT));
    }

    @Test
    void shouldReturnConst() {
        String permittedVal = FIXTURE.create(String.class);
        ConstSchema schema = ConstSchema.builder().permittedValue(permittedVal).build();
        Object result = generator.generate(schema, JsonNode.ROOT);
        assertThat(result).isEqualTo(permittedVal);
    }
}