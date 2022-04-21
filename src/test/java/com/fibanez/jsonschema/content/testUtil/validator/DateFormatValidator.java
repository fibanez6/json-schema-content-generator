package com.fibanez.jsonschema.content.testUtil.validator;

import org.everit.json.schema.FormatValidator;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Optional;

// TODO Candidate to be deleted
@Deprecated
public final class DateFormatValidator implements FormatValidator {

    private static final String FORMAT_ACCEPTED = "yyyy-MM-dd";

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
                    .appendPattern(FORMAT_ACCEPTED)
                    .toFormatter();

    @Override
    public Optional<String> validate(final String subject) {
        try {
            FORMATTER.parse(subject);
            return Optional.empty();
        } catch (DateTimeParseException e) {
            return Optional.of(String.format("[%s] is not a valid %s. Expected [%s]", subject, formatName(), FORMAT_ACCEPTED));
        }
    }

    @Override
    public String formatName() {
        return "date";
    }
}
