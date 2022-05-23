package com.fibanez.jsonschema.content;

import com.fibanez.jsonschema.content.generator.Generator;
import com.fibanez.jsonschema.content.generator.javaType.CollectionGenerator;
import com.fibanez.jsonschema.content.generator.javaType.ConstantGenerator;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Getter
@Builder
public final class JsonGeneratorConfig {

    // location of common references/definitions schemas
    private final String definitionsPath;

    // for string formats
    private final Map<String, Generator<String>> stringFormatGenerators;

    // for string contentType
    private final Map<String, Generator<String>> stringContentTypeGenerators;

    // for java types
    private final Map<Class<?>, Generator<?>> javaTypeGenerators;

    // for predefined property names or paths
    private final Map<String, Supplier<?>> predefinedValueGenerators;

    // only generates required properties
    private final boolean onlyRequiredProps;

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
    private final Integer stringLengthMax;

    // for numbers
    private final Number numberMin;
    private final Number numberMax;

    // for array type
    private final Integer arrayItemsMin;
    private final Integer arrayItemsMax;

    Generator<String> getFormatGenerator(String format) {
        return stringFormatGenerators.get(format);
    }

    Generator<String> getContentTypeGenerator(String format) {
        return stringContentTypeGenerators.get(format);
    }

    @SuppressWarnings("unchecked")
    <T> Generator<T> getJavaTypeGenerator(Class<T> javaTypeClass) {
        return (Generator<T>) javaTypeGenerators.get(javaTypeClass);
    }

    Supplier<?> getPredefinedValueGenerators(String propertyNameOrPath) {
        return predefinedValueGenerators.get(propertyNameOrPath);
    }

    public static class JsonGeneratorConfigBuilder {

        private Map<String, Generator<String>> stringFormatGenerators = new HashMap<>();
        private Map<String, Generator<String>> stringContentTypeGenerators = new HashMap<>();
        private Map<Class<?>, Generator<?>> javaTypeGenerators = new HashMap<>();
        private Map<String, Supplier<?>> predefinedValueGenerators = new HashMap<>();
        private DateTimeFormatter dateFormatter;
        private DateTimeFormatter timeFormatter;
        private DateTimeFormatter dateTimeFormatter;
        private boolean onlyRequiredProps;

        public JsonGeneratorConfigBuilder stringFormatGenerators(Map<String, Generator<String>> generators) {
            this.stringFormatGenerators.putAll(generators);
            return this;
        }

        public JsonGeneratorConfigBuilder stringFormatGenerator(String format, Generator<String> generator) {
            this.stringFormatGenerators.put(format, generator);
            return this;
        }

        public JsonGeneratorConfigBuilder stringFormatGenerator(String format, String... values) {
            return stringFormatGenerator(format, Arrays.asList(values));
        }

        public JsonGeneratorConfigBuilder stringFormatGenerator(String format, Collection<String> values) {
            if (values.size() == 1) {
                this.stringFormatGenerators.put(format, new ConstantGenerator<>(values.iterator().next()));
            } else if (values.size() > 1) {
                this.stringFormatGenerators.put(format, new CollectionGenerator<>(values));
            }
            return this;
        }

        public JsonGeneratorConfigBuilder stringContentTypeGenerators(Map<String, Generator<String>> generators) {
            this.stringContentTypeGenerators.putAll(generators);
            return this;
        }

        public JsonGeneratorConfigBuilder stringContentTypeGenerator(String format, Generator<String> generator) {
            this.stringContentTypeGenerators.put(format, generator);
            return this;
        }

        public JsonGeneratorConfigBuilder stringContentTypeGenerator(String format, String... values) {
            return stringContentTypeGenerator(format, Arrays.asList(values));
        }

        public JsonGeneratorConfigBuilder stringContentTypeGenerator(String format, Collection<String> values) {
            if (values.size() == 1) {
                this.stringContentTypeGenerators.put(format, new ConstantGenerator<>(values.iterator().next()));
            } else if (values.size() > 1) {
                this.stringContentTypeGenerators.put(format, new CollectionGenerator<>(values));
            }
            return this;
        }

        public JsonGeneratorConfigBuilder javaTypeGenerators(Map<Class<?>, Generator<?>> generators) {
            this.javaTypeGenerators.putAll(generators);
            return this;
        }

        public <T> JsonGeneratorConfigBuilder javaTypeGenerator(Class<T> clazz, Generator<T> generator) {
            this.javaTypeGenerators.put(clazz, generator);
            return this;
        }

        public <T> JsonGeneratorConfigBuilder javaTypeGenerator(Class<T> clazz, T... values) {
            return javaTypeGenerator(clazz, Arrays.asList(values));
        }

        public <T> JsonGeneratorConfigBuilder javaTypeGenerator(Class<T> clazz, Collection<T> values) {
            if (values.size() == 1) {
                this.javaTypeGenerators.put(clazz, new ConstantGenerator<>(values.iterator().next()));
            } else if (values.size() > 1) {
                this.javaTypeGenerators.put(clazz, new CollectionGenerator<>(values));
            }
            return this;
        }

        public JsonGeneratorConfigBuilder predefinedValueGenerators(Map<String, ? extends Supplier<?>> generators) {
            this.predefinedValueGenerators.putAll(generators);
            return this;
        }

        public JsonGeneratorConfigBuilder predefinedValueGenerator(String propertyOrPath, Supplier<?> generator) {
            this.predefinedValueGenerators.put(propertyOrPath, generator);
            return this;
        }

        public JsonGeneratorConfigBuilder predefinedValueGenerator(String propertyOrPath, Object... values) {
            return predefinedValueGenerator(propertyOrPath, Arrays.asList(values));
        }

        public JsonGeneratorConfigBuilder predefinedValueGenerator(String propertyOrPath, Collection<Object> values) {
            if (values.size() == 1) {
                this.predefinedValueGenerators.put(propertyOrPath, new ConstantGenerator<>(values.iterator().next()));
            } else if (values.size() > 1) {
                this.predefinedValueGenerators.put(propertyOrPath, new CollectionGenerator<>(values));
            }
            return this;
        }

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

        public JsonGeneratorConfigBuilder onlyRequiredProps() {
            this.onlyRequiredProps = true;
            return this;
        }
    }
}
