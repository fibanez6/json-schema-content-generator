package com.fibanez.jsonschema.content;

import com.fibanez.jsonschema.content.generator.Generator;
import com.fibanez.jsonschema.content.generator.JsonNode;
import com.fibanez.jsonschema.content.generator.SchemaGenerator;
import com.fibanez.jsonschema.content.generator.contentType.ContentType;
import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import com.fibanez.jsonschema.content.generator.javaType.JavaTypeGenerator;
import com.fibanez.jsonschema.content.generator.stringFormat.FormatGenerator;
import lombok.Getter;
import lombok.NonNull;
import net.andreinc.mockneat.unit.user.Names;
import org.everit.json.schema.Schema;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.fibanez.jsonschema.content.generator.util.ReflectionUtils.getDefaultInstanceOf;
import static com.fibanez.jsonschema.content.generator.util.ReflectionUtils.getSubClassesOf;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.Validate.isTrue;

/**
 * The context is saved in a thread-local
 */
@Getter
public class Context {

    public static final String DEFAULT_INITIAL_PATH = "$";
    public static final int DEFAULT_STRING_LENGTH_MIN = 5;
    public static final int DEFAULT_STRING_LENGTH_MAX = 20;
    public static final int DEFAULT_STRING_LENGTH_MARGIN = 10;
    public static final int DEFAULT_NUMBER_MIN = 0;
    public static final int DEFAULT_NUMBER_MAX = Integer.MAX_VALUE;
    public static final int DEFAULT_ARRAY_ITEMS_MIN = 1;
    public static final int DEFAULT_ARRAY_ITEMS_MAX = 5;
    public static final int DEFAULT_ARRAY_ITEMS_MARGIN = 5;
    public static final Number DEFAULT_NUMBER_MULTIPLE_OF = 1;
    public static final String UNPROCESSED_NOT_MULTIPLE_OF = "NOT_MULTIPLE_OF";
    public static final LocalDate DEFAULT_LOCAL_DATE_FROM = LocalDate.of(1900, 1, 1);
    public static final LocalDate DEFAULT_LOCAL_DATE_TO = LocalDate.of(2999, 12, 31);
    public static final LocalTime DEFAULT_LOCAL_TIME_FROM = LocalTime.MIN;
    public static final LocalTime DEFAULT_LOCAL_TIME_TO = LocalTime.MAX;
    public static final LocalDateTime DEFAULT_LOCAL_DATE_TIME_FROM = LocalDateTime.of(DEFAULT_LOCAL_DATE_FROM, DEFAULT_LOCAL_TIME_FROM);
    public static final LocalDateTime DEFAULT_LOCAL_DATE_TIME_TO = LocalDateTime.of(DEFAULT_LOCAL_DATE_TO, DEFAULT_LOCAL_TIME_TO);
    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    public static final DateTimeFormatter DEFAULT_TIME_FORMATTER = DateTimeFormatter.ISO_TIME;
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC);
    public static final Duration DEFAULT_DURATION_FROM = Duration.ZERO;
    public static final Duration DEFAULT_DURATION_TO = Duration.ofDays(10);

    private static final ThreadLocal<Context> THREAD_LOCAL = new ThreadLocal<>();

    // location of common references/definitions schemas
    private final String definitionsPath;

    // only generates required properties
    private final boolean isOnlyRequiredProps;

    // date format
    private final DateTimeFormatter dateFormatter;
    private final LocalDate dateFrom;
    private final LocalDate dateTo;

    // time format
    private final DateTimeFormatter timeFormatter;
    private final LocalTime timeFrom;
    private final LocalTime timeTo;

    // dateTime format
    private final DateTimeFormatter dateTimeFormatter;
    private final LocalDateTime dateTimeFrom;
    private final LocalDateTime dateTimeTo;

    // duration format
    private final Duration durationFrom;
    private final Duration durationTo;

    // for strings
    private final Integer stringLengthMin;
    private final Integer stringLengthMax;
    private final Integer stringLengthMargin;

    // for numbers
    private final Number numberMin;
    private final Number numberMax;

    // for array type
    private final Integer arrayItemsMin;
    private final Integer arrayItemsMax;
    private final Integer arrayItemsMargin;

    // for schemas
    private final Map<Class<Schema>, SchemaGenerator<? extends Schema>> schemaGenerators;

    // for string formats
    private final Map<String, Generator<String>> stringFormatGenerators;

    // for string contentType
    private final Map<String, Generator<String>> stringContentTypeGenerators;

    // for java types
    private final Map<Class<?>, Generator<?>> javaTypeGenerators;

    // for predefined property names or paths
    private final Map<String, Supplier<?>> predefinedValueGenerators;

    public Context(JsonGeneratorConfig config) {
        this.definitionsPath = config.getDefinitionsPath();
        this.isOnlyRequiredProps = config.isOnlyRequiredProps();

        // Date, Time and DateTime formats - must be defined before calling getDefaultStringFormatGenerators method
        this.dateFormatter = ofNullable(config.getDateFormatter()).orElse(DEFAULT_DATE_FORMATTER);
        this.dateFrom = ofNullable(config.getDateFrom()).orElse(DEFAULT_LOCAL_DATE_FROM);
        this.dateTo = ofNullable(config.getDateTo()).orElse(DEFAULT_LOCAL_DATE_TO);
        isTrue(dateFrom.compareTo(dateTo) <= 0, "Date From '{}' must be smaller or equal to Date To '{}'", dateFrom, dateTo);


        this.timeFormatter = ofNullable(config.getTimeFormatter()).orElse(DEFAULT_TIME_FORMATTER);
        this.timeFrom = ofNullable(config.getTimeFrom()).orElse(DEFAULT_LOCAL_TIME_FROM);
        this.timeTo = ofNullable(config.getTimeTo()).orElse(DEFAULT_LOCAL_TIME_TO);
        isTrue(timeFrom.compareTo(timeTo) <= 0, "Time From '{}' must be smaller or equal to Time To '{}'", timeFrom, timeTo);

        this.dateTimeFormatter = ofNullable(config.getDateTimeFormatter()).orElse(DEFAULT_DATE_TIME_FORMATTER);
        this.dateTimeFrom = ofNullable(config.getDateTimeFrom()).orElse(DEFAULT_LOCAL_DATE_TIME_FROM);
        this.dateTimeTo = ofNullable(config.getDateTimeTo()).orElse(DEFAULT_LOCAL_DATE_TIME_TO);
        isTrue(dateTimeFrom.compareTo(dateTimeTo) <= 0, "Date Time From '{}' must be smaller or equal to Date Time To '{}'", dateTimeFrom, dateTimeTo);

        // Duration - must be defined before calling getDefaultStringFormatGenerators method
        this.durationFrom = ofNullable(config.getDurationFrom()).orElse(DEFAULT_DURATION_FROM);
        this.durationTo = ofNullable(config.getDurationTo()).orElse(DEFAULT_DURATION_TO);
        isTrue(durationFrom.compareTo(durationTo) <= 0, "Duration From '{}' must be smaller or equal to Duration To '{}'", durationFrom, durationTo);

        // String Generator
        this.stringLengthMin = ofNullable(config.getStringLengthMin()).orElse(DEFAULT_STRING_LENGTH_MIN);
        this.stringLengthMax = ofNullable(config.getStringLengthMax()).orElse(DEFAULT_STRING_LENGTH_MAX);
        this.stringLengthMargin = DEFAULT_STRING_LENGTH_MARGIN;
        isTrue(stringLengthMin >= 0, "String Length Min '{}' must be higher or equal to 0", stringLengthMin);
        isTrue(stringLengthMin <= stringLengthMax, "String Length Min '{}' must be smaller or equal to String Length Max '{}'", stringLengthMin, stringLengthMax);

        // Number Generator
        this.numberMin = ofNullable(config.getNumberMin()).orElse(DEFAULT_NUMBER_MIN);
        this.numberMax = ofNullable(config.getNumberMax()).orElse(DEFAULT_NUMBER_MAX);
        isTrue(numberMin.longValue() <= numberMax.longValue(), "Number Min '{}' must be smaller or equal to Number Max '{}'", numberMin, numberMax);

        // Array Generator
        this.arrayItemsMin = ofNullable(config.getArrayItemsMin()).orElse(DEFAULT_ARRAY_ITEMS_MIN);
        this.arrayItemsMax = ofNullable(config.getArrayItemsMax()).orElse(DEFAULT_ARRAY_ITEMS_MAX);
        this.arrayItemsMargin = DEFAULT_ARRAY_ITEMS_MARGIN;
        isTrue(arrayItemsMin >= 0, "Array items Min '{}' must be higher or equal to 0", arrayItemsMin);
        isTrue(arrayItemsMin <= arrayItemsMax, "Array items Min '{}' must be smaller or equal to Array items Max '{}'", arrayItemsMin, arrayItemsMax);

        // Schema generators
        this.schemaGenerators = getDefaultSchemaGenerators();

        // String format generators
        Map<String, Generator<String>> formats = getDefaultStringFormatGenerators();
        formats.putAll(config.getStringFormatGenerators());
        this.stringFormatGenerators = Map.copyOf(formats);

        // String content media type generators
        Map<String, Generator<String>> contentType = getDefaultContentTypeGenerators();
        contentType.putAll(config.getStringContentTypeGenerators());
        this.stringContentTypeGenerators = Map.copyOf(contentType);

        // Java type generators
        Map<Class<?>, Generator<?>> javaTypes = getDefaultJavaTypeGenerators();
        javaTypes.putAll(config.getJavaTypeGenerators());
        this.javaTypeGenerators = Map.copyOf(javaTypes);

        // Predefined property name or path generators
        Map<String, Supplier<?>> predefined = getPredefinedGenerators();
        predefined.putAll(config.getPredefinedValueGenerators());
        this.predefinedValueGenerators = Map.copyOf(predefined);

        THREAD_LOCAL.set(this);
    }

    public static Context current() {
        return THREAD_LOCAL.get();
    }

    @SuppressWarnings("unchecked")
    private Map<Class<Schema>, SchemaGenerator<? extends Schema>> getDefaultSchemaGenerators() {
        Stream<Class<? extends SchemaGenerator>> stream = getSubClassesOf(SchemaGenerator.class);
        return stream
                .map(c -> getDefaultInstanceOf(c, this))
                .collect(toMap(SchemaGenerator::schemaType, Function.identity()));
    }

    /**
     * Read the classes in the {@link com.fibanez.jsonschema.content.generator.stringFormat} package that implement the
     * interface {@link FormatGenerator}
     *
     * @return Collection of formats and their default generators
     */
    private Map<String, Generator<String>> getDefaultStringFormatGenerators() {
        Stream<Class<? extends FormatGenerator>> stream = getSubClassesOf(FormatGenerator.class);
        return stream
                .map(c -> getDefaultInstanceOf(c, this))
                .collect(toMap(FormatGenerator::format, Function.identity()));
    }

    /**
     * Read the classes in the {@link com.fibanez.jsonschema.content.generator.stringFormat} package that implement the
     * interface {@link ContentType}
     *
     * @return Collection of formats and their default generators
     */
    private Map<String, Generator<String>> getDefaultContentTypeGenerators() {
        Stream<Class<? extends ContentType>> stream = getSubClassesOf(ContentType.class);
        return stream
                .map(c -> getDefaultInstanceOf(c, this))
                .collect(toMap(ContentType::mimeType, Function.identity()));
    }

    /**
     * @return Collection of Java types and their default generators
     */
    @SuppressWarnings("unchecked")
    private Map<Class<?>, Generator<?>> getDefaultJavaTypeGenerators() {
        Stream<Class<? extends JavaTypeGenerator>> stream = getSubClassesOf(JavaTypeGenerator.class);
        return stream
                .map(c -> getDefaultInstanceOf(c, this))
                .collect(toMap(JavaTypeGenerator::classType, Function.identity()));
    }

    private Map<String, Supplier<?>> getPredefinedGenerators() {
        Map<String, Supplier<?>> values = new HashMap<>();
        values.put("name", Names.names().supplier());
        values.put("firstName", Names.names().supplier());
        values.put("lastName", Names.names().supplier());
        return values;
    }

    public static SchemaGenerator<? extends Schema> getSchemaGenerator(@NonNull Class<? extends Schema> schemaClass) {
        Map<Class<Schema>, SchemaGenerator<? extends Schema>> schemaGenerators = current().getSchemaGenerators();
        if (schemaGenerators.containsKey(schemaClass)) {
            return schemaGenerators.get(schemaClass);
        }
        throw new GeneratorException("Schema generator not found for: " + schemaClass);
    }

    public static FormatGenerator getFormatGenerator(@NonNull String format) {
        Map<String, Generator<String>> stringFormatGenerators = current().getStringFormatGenerators();
        if (stringFormatGenerators.containsKey(format)) {
            return (FormatGenerator) stringFormatGenerators.get(format);
        }
        throw new GeneratorException("String format generator not found for: " + format);
    }

    public static ContentType getContentMediaTypeGenerator(@NonNull String contentType) {
        Map<String, Generator<String>> stringContentTypeGenerators = current().getStringContentTypeGenerators();
        Optional<Map.Entry<String, Generator<String>>> entry = stringContentTypeGenerators.entrySet().stream()
                .filter(e -> contentType.matches(e.getKey().replace("/*", "\\/.*")))
                .findFirst();
        if (entry.isPresent()) {
            return (ContentType) entry.get().getValue();
        }
        throw new GeneratorException("String format generator not found for: " + contentType);
    }

    @SuppressWarnings("unchecked")
    public static <T> JavaTypeGenerator<T> getJavaTypeGenerator(@NonNull Class<T> type) {
        Map<Class<?>, Generator<?>> javaTypeGenerators = current().getJavaTypeGenerators();
        if (javaTypeGenerators.containsKey(type)) {
            return (JavaTypeGenerator<T>) javaTypeGenerators.get(type);
        }
        throw new GeneratorException("Java type generator not found for: " + type);
    }

    public static Optional<Supplier<?>> getPredefinedValueGenerator(@NonNull JsonNode jsonNode) {
        Map<String, Supplier<?>> valueGenerators = current().getPredefinedValueGenerators();

        // Path is more priority
        String path = jsonNode.getPath();
        if (Objects.nonNull(path) && valueGenerators.containsKey(path)) {
            return Optional.of(valueGenerators.get(path));
        }
        String propertyName = jsonNode.getPropertyName();
        if (Objects.nonNull(propertyName) && valueGenerators.containsKey(propertyName)) {
            return Optional.of(valueGenerators.get(propertyName));
        }
        return Optional.empty();
    }

}
