package com.fibanez.jsonschema.content.validator;

import lombok.AllArgsConstructor;
import org.everit.json.schema.FormatValidator;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@AllArgsConstructor
public abstract class PatternFormatValidator implements FormatValidator {

    private final Pattern pattern;

    @Override
    public abstract String formatName();

    @Override
    public Optional<String> validate(String subject) {
        if (Objects.nonNull(subject) && pattern.matcher(subject).matches()) {
            return Optional.empty();
        } else {
            return Optional.of(String.format("[%s] is not a valid %s. Expected [%s]", subject, formatName(), pattern.pattern()));
        }
    }
}
