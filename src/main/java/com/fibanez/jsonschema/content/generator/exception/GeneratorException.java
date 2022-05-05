package com.fibanez.jsonschema.content.generator.exception;

import java.util.Formatter;

public class GeneratorException extends RuntimeException {

    public GeneratorException(String message) {
        super(message);
    }

    public GeneratorException(String message, Object... args) {
        super(new Formatter().format(message, args).toString());
    }

    public GeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
