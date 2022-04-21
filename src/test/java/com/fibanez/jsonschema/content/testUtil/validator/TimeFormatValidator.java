package com.fibanez.jsonschema.content.testUtil.validator;

import org.everit.json.schema.FormatValidator;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Optional;

public final class TimeFormatValidator implements FormatValidator {

    private static final String[] FORMAT_ACCEPTED = new String[] {"HH:mm", "HH:mm:ss"};

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
                    .appendPattern("HH:mm")
                    .appendOptional(DateTimeFormatter.ofPattern(":ss"))
                    .toFormatter();

    @Override
    public Optional<String> validate(final String subject) {
        try {
            FORMATTER.parse(subject);
            return Optional.empty();
        } catch (DateTimeParseException e) {
            return Optional.of(String.format("[%s] is not a valid %s. Expected %s", subject, formatName(), Arrays.toString(FORMAT_ACCEPTED)));
        }
    }

    @Override
    public String formatName() {
        return "time";
    }
}
