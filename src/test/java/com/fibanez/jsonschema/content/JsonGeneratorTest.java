package com.fibanez.jsonschema.content;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonGeneratorTest {

    private static JsonGenerator generator;

    @BeforeEach
    void setUp() {
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .definitionsPath("schemas/common/")
                .build();
        generator = new JsonGenerator(config);
    }

    @Test
    void shouldThrowException_whenNullConfig() {
        assertThrows(NullPointerException.class, () -> new JsonGenerator(null));
    }

    @Test
    void shouldThrowException_whenNullInputStream() {
        assertThrows(NullPointerException.class, () -> generator.generate((InputStream) null));
    }

    @Test
    void shouldThrowException_whenNullString() {
        assertThrows(NullPointerException.class, () -> generator.generate((String) null));
    }

    @Test
    void shouldGenerateJSONObject_fromInputStream() {
        String schemaPath = "schemas/simple_schema.json";
        String schema = getResource(schemaPath);
        InputStream inputStream = getResourceAsStream(schemaPath);

        JSONObject jsonObject = generator.generate(inputStream);
        validate(schema, jsonObject);
    }

    @Timeout(10)
    @ParameterizedTest(name = "#{index} - Generate data for {0}")
    @MethodSource("provideSchemaResourcePath")
    void shouldGenerateJSONObject_forEachSchema(Path schemaPath) throws IOException {
        String schema = Files.readString(schemaPath);
        JSONObject jsonObject = generator.generate(schema);
        System.out.println(jsonObject.toString());
        validate(schema, jsonObject);
    }

    @Test
    void shouldGenerateJSONObject_onlyRequireProps() {
        // no required radius
        String jsonSchema = "" +
                "{" +
                "  \"type\": \"object\"," +
                "  \"additionalProperties\": false," +
                "  \"required\": [ \"shape\" ]," +
                "  \"properties\": {" +
                "    \"shape\": {" +
                "      \"type\": \"object\"," +
                "      \"additionalProperties\": false," +
                "      \"required\": [ \"name\", \"area\"]," +
                "      \"properties\": {" +
                "        \"name\": { \"type\": \"string\" }," +
                "        \"radius\": { \"type\": \"number\", \"minimum\": 0 }," +
                "        \"area\": { \"type\": \"number\"}" +
                "      }" +
                "    }" +
                "  }" +
                "}";

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .onlyRequiredProps()
                .build();

        JSONObject jsonObject = new JsonGenerator(config).generate(jsonSchema);
        validate(jsonSchema, jsonObject);
        assertFalse(jsonObject.getJSONObject("shape").has("radius"));
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
