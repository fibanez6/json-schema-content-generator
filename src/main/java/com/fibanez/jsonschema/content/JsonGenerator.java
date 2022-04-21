package com.fibanez.jsonschema.content;

import com.fibanez.jsonschema.content.generator.CrumbPath;
import com.fibanez.jsonschema.content.generator.SchemaGenerator;
import com.fibanez.jsonschema.content.validator.DurationFormatValidator;
import com.fibanez.jsonschema.content.validator.UuidFormatValidator;
import org.apache.commons.lang3.StringUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.Objects;

public final class JsonGenerator {

    private final Context context;

    public JsonGenerator() {
        this(JsonGeneratorConfig.builder().build());
    }

    public JsonGenerator(JsonGeneratorConfig config) {
        Objects.requireNonNull(config, "Config cannot be null");
        this.context = new Context(config);
    }

    public JSONObject generate(InputStream inputStream) {
        Objects.requireNonNull(inputStream, "InputStream cannot be null");
        JSONObject jsonObject = new JSONObject(new JSONTokener(inputStream));
        return generate(jsonObject);
    }

    public JSONObject generate(String jsonSchema) {
        Objects.requireNonNull(jsonSchema, "JsonSchema cannot be null");
        JSONObject jsonObject = new JSONObject(new JSONTokener(jsonSchema));
        return generate(jsonObject);
    }

    private JSONObject generate(JSONObject jsonSchema) {
        Schema schema = getSchema(jsonSchema);
        SchemaGenerator generator = Context.getSchemaGenerator(schema.getClass());
        return (JSONObject) generator.generate(schema, CrumbPath.ROOT);
    }

    private Schema getSchema(JSONObject schemaJson) {
        return getSchemaBuilder()
                .schemaJson(schemaJson)
                .build().load().build();
    }

    private SchemaLoader.SchemaLoaderBuilder getSchemaBuilder() {
        SchemaLoader.SchemaLoaderBuilder builder = SchemaLoader.builder();
        builder.schemaClient(SchemaClient.classPathAwareClient());
        String classpath = context.getDefinitionsPath();
        if (StringUtils.isNotEmpty(classpath)) {
            builder.resolutionScope("classpath://" + classpath);
        }
        builder.draftV6Support();
        builder.draftV7Support();
        builder.addFormatValidator(new DurationFormatValidator());
        builder.addFormatValidator(new UuidFormatValidator());
        return builder;
    }
}
