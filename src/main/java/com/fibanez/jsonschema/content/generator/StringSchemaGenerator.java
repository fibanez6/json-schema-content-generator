package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.abstraction.RangeGenerator;
import com.fibanez.jsonschema.content.generator.stringFormat.FormatGenerator.Format;
import com.fibanez.jsonschema.content.generator.stringFormat.RegexGenerator;
import lombok.NonNull;
import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.StringSchema;

import java.util.Objects;
import java.util.Optional;

public final class StringSchemaGenerator implements SchemaGenerator<StringSchema> {

    public static final String UNNAMED_FORMAT = "unnamed-format";

    @Override
    public String generate(@NonNull StringSchema schema, @NonNull CrumbPath crumbPath) {

        Generator<String> generator = getGenerator(schema);
        if (generator instanceof RegexGenerator) {
            RegexGenerator regexGenerator = (RegexGenerator) generator;
            return regexGenerator.get(schema.getPattern().pattern());
        }
        else if (generator instanceof RangeGenerator) {
            @SuppressWarnings("unchecked")
            RangeGenerator<Integer, String> rangeGenerator = (RangeGenerator<Integer, String>) generator;
            int minLength = getOrDefault(schema.getMinLength(), Context.context().getStringLengthMin());
            int maxLength = getOrDefault(schema.getMaxLength(), Context.context().getStringLengthMax());
            return rangeGenerator.get(minLength, maxLength);
        }

        return generator.get();
    }

    private Generator<String> getGenerator(StringSchema schema) {
        if (Objects.nonNull(schema.getPattern())) {
            return Context.getFormatGenerator(Format.REGEX.value());
        }

        Optional<String> formatName = getFormatName(schema);
        if (formatName.isPresent()) {
            return Context.getFormatGenerator(formatName.get());
        }

        return Context.getJavaTypeGenerator(String.class);
    }

    private Optional<String> getFormatName(StringSchema schema) {
        FormatValidator formatValidator = schema.getFormatValidator();
        if (Objects.isNull(formatValidator) || UNNAMED_FORMAT.equals(formatValidator.formatName())) {
            return Optional.empty();
        }
        return Optional.of(formatValidator.formatName());
    }

}
