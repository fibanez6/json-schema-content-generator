package com.fibanez.jsonschema.content.generator.exception;

import java.util.Formatter;

public class GeneratorException extends RuntimeException {

    public GeneratorException(String message) {
        super(message);
    }

    public GeneratorException(String message, Object... args) {
        this(new Formatter().format(message, args).toString());
    }

    public GeneratorException(Throwable cause, String message, Object... args) {
        this(cause, new Formatter().format(message, args).toString());
    }

    public GeneratorException(Throwable cause, String message) {
        super(message, cause);
    }
}
