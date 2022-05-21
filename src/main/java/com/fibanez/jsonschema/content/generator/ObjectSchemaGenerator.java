package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.Schema;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ObjectSchemaGenerator implements SchemaGenerator<ObjectSchema> {

    @Override
    public JSONObject generate(@NonNull ObjectSchema schema, @NonNull JsonNode jsonNode) {
        Context ctx = Context.current();
        Map<String, Schema> propertySchemas = schema.getPropertySchemas();
        Stream<Map.Entry<String, Schema>> entries = propertySchemas.entrySet().stream();
        JSONObject jsonObject = new JSONObject();

        if (ctx.isOnlyRequiredProps()) {
            List<String> requiredProps = schema.getRequiredProperties();
            entries = entries.filter(entry -> requiredProps.contains(entry.getKey()));
        }

        entries.forEach(entry -> {
            Schema innerSchema = entry.getValue();
            String propertyName = entry.getKey();

            JsonNode nextJsonNode = jsonNode.getNext(propertyName);
            Object generated = generateFrom(innerSchema, nextJsonNode);
            jsonObject.put(entry.getKey(), generated);
        });

        return jsonObject;
    }
}
