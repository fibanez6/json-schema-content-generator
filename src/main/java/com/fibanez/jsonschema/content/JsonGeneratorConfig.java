package com.fibanez.jsonschema.content;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Getter
@Builder
public final class JsonGeneratorConfig {

    // location of common references/definitions schemas
    private final String definitionsPath;

    // for string formats
    private final Map<String, Supplier<String>> stringFormatGenerators;

    // for java types
    private final Map<Class<?>, Supplier<?>> javaTypeGenerators;

    // only generates required properties
    private final boolean isOnlyRequiredProps;

    // string Format of the date
    private final DateTimeFormatter dateFormatter;
    private final LocalDate dateFrom;
    private final LocalDate dateTo;

    // string Format of the time
    private final DateTimeFormatter timeFormatter;
    private final LocalTime timeFrom;
    private final LocalTime timeTo;

    // string Format of the dateTime
    private final DateTimeFormatter dateTimeFormatter;
    private final LocalDateTime dateTimeFrom;
    private final LocalDateTime dateTimeTo;

    // duration format
    private final Duration durationFrom;
    private final Duration durationTo;

    // for strings
    private final Integer stringLengthMin;
    private final Integer StringLengthMax;

    // for numbers
    private final Number numberMin;
    private final Number numberMax;

    // for array type
    private final Integer arrayItemsMin;
    private final Integer arrayItemsMax;

    Supplier<String> getFormatGenerator(String format) {
        return stringFormatGenerators.get(format);
    }

    @SuppressWarnings("unchecked")
    <T> Supplier<T> getJavaTypeGenerator(Class<T> javaTypeClass) {
        return (Supplier<T>) javaTypeGenerators.get(javaTypeClass);
    }

    public static class JsonGeneratorConfigBuilder {

        private DateTimeFormatter dateFormatter;
        private DateTimeFormatter timeFormatter;
        private DateTimeFormatter dateTimeFormatter;

        private Map<String, Supplier<String>> stringFormatGenerators = new HashMap<>();
        private Map<Class<?>, Supplier<?>> javaTypeGenerators = new HashMap<>();
        private Map<String, Integer> stringLengthPropertyMin = new HashMap<>();
        private Map<String, Integer> stringLengthPropertyMax = new HashMap<>();

        public JsonGeneratorConfigBuilder dateFormatter(String pattern) {
            this.dateFormatter = DateTimeFormatter.ofPattern(pattern);
            return this;
        }

        public JsonGeneratorConfigBuilder dateTimeFormatter(String pattern) {
            this.dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
            return this;
        }

        public JsonGeneratorConfigBuilder timeFormatter(String pattern) {
            this.timeFormatter = DateTimeFormatter.ofPattern(pattern);
            return this;
        }

        public JsonGeneratorConfigBuilder stringFormatGenerator(Map<String, Supplier<String>> stringFormats) {
            stringFormatGenerators.putAll(stringFormats);
            return this;
        }

        public JsonGeneratorConfigBuilder stringFormatGenerator(String format, Supplier<String> generator) {
            stringFormatGenerators.put(format, generator);
            return this;
        }

        public <T> JsonGeneratorConfigBuilder javaTypeGenerator(Map<Class<?>, Supplier<?>> javaTypes) {
            javaTypeGenerators.putAll(javaTypes);
            return this;
        }

        public <T> JsonGeneratorConfigBuilder javaTypeGenerator(Class<T> javaTypeClass, Supplier<T> generator) {
            javaTypeGenerators.put(javaTypeClass, generator);
            return this;
        }

        public JsonGeneratorConfigBuilder stringLengthPropertyMin(Map<String, Integer> minProperties) {
            stringLengthPropertyMin.putAll(minProperties);
            return this;
        }

        public JsonGeneratorConfigBuilder stringLengthPropertyMin(String name, int length) {
            stringLengthPropertyMin.put(name, length);
            return this;
        }

        public JsonGeneratorConfigBuilder stringLengthPropertyMax(Map<String, Integer> maxProperties) {
            stringLengthPropertyMax.putAll(maxProperties);
            return this;
        }

        public JsonGeneratorConfigBuilder stringLengthPropertyMax(String name, int length) {
            stringLengthPropertyMax.put(name, length);
            return this;
        }
    }
}
