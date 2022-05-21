package com.fibanez.jsonschema.content.generator.schemaMerger;

import org.everit.json.schema.ConstSchema;
import org.everit.json.schema.EnumSchema;
import org.everit.json.schema.NotSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

class SchemaCombinatorTest {

    @ParameterizedTest
    @MethodSource("provideEnumPermutations")
    void shouldCombineAllSchemas_enumPermutations(List<Schema> schemasToCombine, EnumSchema expected) {
        EnumSchema result = (EnumSchema) SchemaCombinator.combine(schemasToCombine);
        assertThat(result.getPossibleValues()).containsExactlyElementsIn(expected.getPossibleValues());
    }

    private static Stream<Arguments> provideEnumPermutations() {
        StringSchema stringSchema = StringSchema.builder().build();
        EnumSchema enumSchema = EnumSchema.builder()
                .possibleValue("Green")
                .possibleValue("Blue")
                .possibleValue("Red")
                .build();
        ConstSchema mustGreen = ConstSchema.builder()
                .permittedValue("Green")
                .build();
        NotSchema mustNotGreen = NotSchema.builder()
                .mustNotMatch(mustGreen)
                .build();
        NotSchema mustNotBlue = NotSchema.builder()
                .mustNotMatch(ConstSchema.builder().permittedValue("Blue").build())
                .build();

        EnumSchema expectedGreen = EnumSchema.builder().possibleValue("Green").build();
        EnumSchema expectedBlueRed = EnumSchema.builder().possibleValue("Blue").possibleValue("Red").build();
        EnumSchema expectedRed = EnumSchema.builder().possibleValue("Red").build();

        return Stream.of(
                Arguments.of(List.of(stringSchema, enumSchema, mustGreen), expectedGreen),
                Arguments.of(List.of(stringSchema, mustGreen, enumSchema), expectedGreen),
                Arguments.of(List.of(enumSchema, stringSchema, mustGreen), expectedGreen),
                Arguments.of(List.of(enumSchema, mustGreen, stringSchema), expectedGreen),
                Arguments.of(List.of(mustGreen, stringSchema, enumSchema), expectedGreen),
                Arguments.of(List.of(mustGreen, enumSchema, stringSchema), expectedGreen),

                Arguments.of(List.of(stringSchema, enumSchema, mustNotGreen), expectedBlueRed),
                Arguments.of(List.of(stringSchema, mustNotGreen, enumSchema), expectedBlueRed),
                Arguments.of(List.of(enumSchema, stringSchema, mustNotGreen), expectedBlueRed),
                Arguments.of(List.of(enumSchema, mustNotGreen, stringSchema), expectedBlueRed),
                Arguments.of(List.of(mustNotGreen, stringSchema, enumSchema), expectedBlueRed),
                Arguments.of(List.of(mustNotGreen, enumSchema, stringSchema), expectedBlueRed),

                Arguments.of(List.of(stringSchema, enumSchema, mustNotGreen, mustNotBlue), expectedRed),
                Arguments.of(List.of(stringSchema, mustNotGreen, enumSchema, mustNotBlue), expectedRed),
                Arguments.of(List.of(stringSchema, mustNotGreen, mustNotBlue, enumSchema), expectedRed),
                Arguments.of(List.of(enumSchema, stringSchema, mustNotGreen, mustNotBlue), expectedRed),
                Arguments.of(List.of(enumSchema, mustNotGreen, stringSchema, mustNotBlue), expectedRed),
                Arguments.of(List.of(enumSchema, mustNotGreen, mustNotBlue, stringSchema), expectedRed),
                Arguments.of(List.of(mustNotGreen, stringSchema, enumSchema, mustNotBlue), expectedRed),
                Arguments.of(List.of(mustNotGreen, stringSchema, mustNotBlue, enumSchema), expectedRed),
                Arguments.of(List.of(mustNotGreen, mustNotBlue, stringSchema, enumSchema), expectedRed)
        );
    }
}