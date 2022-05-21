package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.everit.json.schema.NotSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class NotSchemaGenerator implements SchemaGenerator<NotSchema> {

    @Override
    public Object generate(@NonNull NotSchema schema, JsonNode jsonNode) {
        Schema mustNotMatch = schema.getMustNotMatch();
        Generator<?> generator;
        if (mustNotMatch instanceof StringSchema) {
            generator = Context.current().getJavaTypeGenerators().entrySet().stream()
                    .filter(e -> !e.getKey().equals(String.class))
                    .map(Map.Entry::getValue)
                    .findAny()
                    .get();
        } else {
            generator = Context.getJavaTypeGenerator(String.class);
        }
        return generator.get();
    }

}
