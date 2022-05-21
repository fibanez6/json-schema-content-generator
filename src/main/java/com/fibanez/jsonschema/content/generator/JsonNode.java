package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import lombok.Getter;
import lombok.NonNull;

@Getter
public final class JsonNode {

    public static final JsonNode ROOT = new JsonNode(Context.DEFAULT_INITIAL_PATH, null, false, null);
    private static final String DELIMITER = ".";
    private static final String ARRAY_START_DELIMITER = "[";
    private static final String ARRAY_END_DELIMITER = "]";

    private final String path;

    private final String propertyName;

    private final boolean isArray;

    private final JsonNode previous;

    private JsonNode(String path, String propertyName, boolean isArray, JsonNode previous) {
        this.path = path;
        this.propertyName = propertyName;
        this.isArray = isArray;
        this.previous = previous;
    }

    JsonNode getNext(@NonNull String propertyName) {
        String nextPath = path + DELIMITER + propertyName;
        return new JsonNode(nextPath, propertyName, false, this);
    }

    JsonNode getArrayNext(int index) {
        String arrayIndex = ARRAY_START_DELIMITER + index + ARRAY_END_DELIMITER;
        String arrayPath = path + arrayIndex;
        return new JsonNode(arrayPath, propertyName, true, previous);
    }

}
