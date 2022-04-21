package com.fibanez.jsonschema.content.generator.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SchemaUtils {

    private static JSONObject getSchema(Map<String, Object> map) {

        return new JSONObject();
    }

}
