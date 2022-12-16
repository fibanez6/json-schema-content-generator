package com.fibanez.jsonschema.content.generator.util;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class FileUtils {

    public static byte[] getImageResource(String resourceName) {
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
            return inputStream.readAllBytes();
        } catch (Exception ex) {
            throw new GeneratorException(ex, "Resource not found %s", resourceName);
        }
    }

}
