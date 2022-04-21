package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

@Getter
public final class CrumbPath {

    public static final CrumbPath ROOT = new CrumbPath(Context.DEFAULT_INITIAL_PATH, null, null);
    private static final String DELIMITER = ".";
    private static final String ARRAY_START_DELIMITER = "[";
    private static final String ARRAY_END_DELIMITER = "]";

    private final String path;

    private final String propertyName;

    private final CrumbPath previous;

    private CrumbPath(String path, String propertyName, CrumbPath previous) {
        this.propertyName = propertyName;
        this.previous = previous;
        this.path = path;
    }

    static CrumbPath getNext(@NonNull String propertyName, @NonNull CrumbPath previous) {
        String path = previous.path + DELIMITER + propertyName;
        return new CrumbPath(path, propertyName, previous);
    }

    static CrumbPath getArrayNext(int index, @NonNull CrumbPath previous) {
        String arrayIndex = ARRAY_START_DELIMITER + index + ARRAY_END_DELIMITER;
        String arrayPath = previous.path + arrayIndex;
        String arrayPropertyName= Objects.isNull(previous.propertyName)
                ? previous.path + arrayIndex
                : previous.propertyName + arrayIndex;
        return new CrumbPath(arrayPath, arrayPropertyName, previous.previous);
    }

}
