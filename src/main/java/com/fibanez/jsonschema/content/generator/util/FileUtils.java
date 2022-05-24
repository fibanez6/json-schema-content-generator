package com.fibanez.jsonschema.content.generator.util;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class FileUtils {

    public static byte[] getImageResource(String resourceName) {
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
            Path path = Path.of(url.toURI());
            return Files.readAllBytes(path);
        } catch (Exception ex) {
            throw new GeneratorException(ex, "Resource not found %s", resourceName);
        }
    }

}
