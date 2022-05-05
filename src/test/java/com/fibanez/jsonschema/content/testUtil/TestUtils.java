package com.fibanez.jsonschema.content.testUtil;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.JsonGeneratorConfig;
import com.fibanez.jsonschema.content.generator.JsonNode;
import com.fibanez.jsonschema.content.testUtil.validator.TimeFormatValidator;
import com.fibanez.jsonschema.content.validator.DurationFormatValidator;
import com.fibanez.jsonschema.content.validator.UuidFormatValidator;
import com.flextrade.jfixture.JFixture;
import org.apache.commons.lang3.StringUtils;
import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;

public final class TestUtils {

    private TestUtils() {
        // Do Nothing
    }

    public static final JFixture FIXTURE = new JFixture();
    public static final JsonGeneratorConfig DEFAULT_CONFIG = JsonGeneratorConfig.builder().build();

    public static Context createContext() {
        return createContext(DEFAULT_CONFIG);
    }

    public static Context createContext(JsonGeneratorConfig config) {
        return new Context(config);
    }

    public static void validate(String schemaStr, JSONObject payload) {
        assertFalse(payload.isEmpty());
        JSONObject jsonSchema = new JSONObject(new JSONTokener(schemaStr));
        Schema schema = getSchema(jsonSchema, "schemas/common/");
        validate(schema, payload);
    }

    public static void validate(Schema schema, JSONObject payload) {
        try {
            schema.validate(payload);
        } catch (ValidationException ex) {
            String error = "\nValidation error messages:\n\t" + String.join("\n\t", ex.getAllMessages());
            Assertions.fail(error);
        }
    }

    public static void validate(FormatValidator formatValidator, String value) {
        Optional<String> validationResult = formatValidator.validate(value);
        validationResult.ifPresent(Assertions::fail);
    }

    public static Schema getSchema(JSONObject jsonSchema, String classpath) {
        SchemaLoader.SchemaLoaderBuilder builder = SchemaLoader.builder();
        if (StringUtils.isNotEmpty(classpath)) {
            builder.schemaClient(SchemaClient.classPathAwareClient());
            builder.resolutionScope("classpath://" + classpath);
        }
        return builder.schemaJson(jsonSchema)
                .draftV6Support()
                .draftV7Support()
                .enableOverrideOfBuiltInFormatValidators()
                .addFormatValidator(new TimeFormatValidator())
                .addFormatValidator(new DurationFormatValidator())
                .addFormatValidator(new UuidFormatValidator())
                .build().load().build();
    }

    public static String getResource(String resourceName) {
        try {
            Path path = getResourcePath(resourceName);
            return Files.readString(path);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static InputStream getResourceAsStream(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
    }

    public static Path getResourcePath(String resource) throws URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
        URI uri = Objects.requireNonNull(url).toURI();
        return Path.of(uri);
    }

    public static JsonNode createJsonNode(String path, String propertyName) throws Exception {
        Constructor constructor = JsonNode.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        return (JsonNode) constructor.newInstance(path, propertyName, false, null);
    }
}
