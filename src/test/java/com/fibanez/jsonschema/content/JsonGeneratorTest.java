package com.fibanez.jsonschema.content;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.getResource;
import static com.fibanez.jsonschema.content.testUtil.TestUtils.getResourceAsStream;
import static com.fibanez.jsonschema.content.testUtil.TestUtils.getResourcePath;
import static com.fibanez.jsonschema.content.testUtil.TestUtils.validate;
import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonGeneratorTest {

    @Test
    void shouldThrowException_whenNullConfig() {
        assertThrows(NullPointerException.class, () -> new JsonGenerator(null));
    }

    @Test
    void shouldThrowException_whenNullInputStream() {
        assertThrows(NullPointerException.class, () -> getGenerator().generate((InputStream) null));
    }

    @Test
    void shouldThrowException_whenNullString() {
        assertThrows(NullPointerException.class, () -> getGenerator().generate((String) null));
    }

    @Test
    void shouldGenerateJSONObject_fromInputStream() {
        String schemaPath = "schemas/simple_schema.json";
        String schema = getResource(schemaPath);
        InputStream inputStream = getResourceAsStream(schemaPath);

        JSONObject jsonObject = getGenerator().generate(inputStream);
        validate(schema, jsonObject);
    }

    @Timeout(10)
    @ParameterizedTest(name = "#{index} - Generate data for {0}")
    @MethodSource("provideSchemaResourcePath")
    void shouldGenerateJSONObject_forEachSchema(Path schemaPath) throws IOException {
        String schema = Files.readString(schemaPath);
        JSONObject jsonObject = getGenerator().generate(schema);
        validate(schema, jsonObject);
    }

    private JsonGenerator getGenerator() {
        return new JsonGenerator();
    }

    private static Stream<Arguments> provideSchemaResourcePath() throws IOException, URISyntaxException {
        Path path = getResourcePath("schemas");
        int pathNameCount = path.getNameCount();

        Set<String> ignoreSchema = new HashSet<>();
        ignoreSchema.add("schemas/draft_2019-09/array_schema.json");
        ignoreSchema.add("schemas/test_schema.json");
        ignoreSchema.add("schemas/test2_schema.json");

        Predicate<Path> isJsonSchema = p -> Files.isRegularFile(p) && p.getFileName().toString().endsWith(".json");
        Predicate<Path> isDraft2019_09 = p -> "draft_2019-09".equals(p.getName(p.getNameCount()-2).toString());
        Predicate<Path> isDraft2020_12 = p -> "draft_2020-12".equals(p.getName(p.getNameCount()-2).toString());
        Predicate<Path> isIgnoredSchema = p -> ignoreSchema.contains(p.subpath(pathNameCount-1, p.getNameCount()).toString());

        return Files.walk(path)
                .peek(System.out::println)
                .peek(p -> p.getFileName())
                .filter(isJsonSchema
                        .and( not( isDraft2020_12 ))
                        .and( not( isIgnoredSchema ))
                )
                .map(Arguments::of);
    }
}
