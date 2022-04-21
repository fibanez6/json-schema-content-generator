package com.fibanez.jsonschema.content.validator;

import java.util.regex.Pattern;

public class UuidFormatValidator extends PatternFormatValidator {

    private static final Pattern UUID = Pattern.compile(
                    "^[a-f0-9]{8}-[a-f0-9]{4}-[0-5][a-f0-9]{3}-[89ab][a-f0-9]{3}-[a-f0-9]{12}$",
                    Pattern.CASE_INSENSITIVE);

    public UuidFormatValidator() {
        super(UUID);
    }

    @Override
    public String formatName() {
        return "uuid";
    }
}
