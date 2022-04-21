package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import lombok.NonNull;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.Schema;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class ObjectSchemaGenerator implements SchemaGenerator<ObjectSchema> {

    @Override
    public Object generate(@NonNull ObjectSchema schema, @NonNull CrumbPath crumbPath) {
        Context ctx = Context.context();
        JSONObject jsonObject = new JSONObject();
        Map<String, Schema> propertySchemas = schema.getPropertySchemas();
        Stream<Map.Entry<String, Schema>> entries = propertySchemas.entrySet().stream();

        if (ctx.isOnlyRequiredProps()) {
            List<String> requiredProps = schema.getRequiredProperties();
            entries = entries.filter(entry -> requiredProps.contains(entry.getKey()));
        }

        entries.forEach(entry -> {
            Schema innerSchema = entry.getValue();
            String propertyName = entry.getKey();

            CrumbPath nextCrumbPath = CrumbPath.getNext(propertyName, crumbPath);
            Object generated = generateFrom(innerSchema, nextCrumbPath);
            jsonObject.put(entry.getKey(), generated);
        });

        return jsonObject;
    }

}
