package com.fibanez.jsonschema.content.testUtil.validator;

import org.everit.json.schema.FormatValidator;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Optional;
// TODO Candidate to be deleted
@Deprecated
public final class DateTimeFormatValidator implements FormatValidator {

    private static final String FORMAT_ACCEPTED = "yyyy-MM-dd'T'HH:mm:ss.[0-9]{3}'Z'";

    private static final String PARTIAL_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    private static final String ZONE_OFFSET_PATTERN = "'Z'";

    private static final DateTimeFormatter FORMATTER;

    static {
        final DateTimeFormatter secondsFractionFormatter = new DateTimeFormatterBuilder()
                        .appendFraction(ChronoField.NANO_OF_SECOND, 3, 3, true)
                        .toFormatter();

        final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()
                        .appendPattern(PARTIAL_DATETIME_PATTERN)
                        .append(secondsFractionFormatter)
                        .appendPattern(ZONE_OFFSET_PATTERN);

        FORMATTER = builder.toFormatter();
    }

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
        return "date-time";
    }
}
