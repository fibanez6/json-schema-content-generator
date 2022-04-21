package com.fibanez.jsonschema.content;

import com.fibanez.jsonschema.content.generator.SchemaGenerator;
import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import com.fibanez.jsonschema.content.generator.javaType.JavaTypeGenerator;
import com.fibanez.jsonschema.content.generator.stringFormat.FormatGenerator;
import com.fibanez.jsonschema.content.generator.util.ReflectionUtils;
import lombok.Getter;
import lombok.NonNull;
import org.everit.json.schema.Schema;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.fibanez.jsonschema.content.generator.util.ReflectionUtils.getClasses;
import static com.fibanez.jsonschema.content.generator.util.ReflectionUtils.getDefaultInstanceOf;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

/**
 * The context is saved in a thread-local
 */
@Getter
public class Context {

    public static final  String DEFAULT_INITIAL_PATH = "$";
    public static final int DEFAULT_STRING_LENGTH_MIN = 5;
    public static final int DEFAULT_STRING_LENGTH_MAX = 20;
    public static final int DEFAULT_NUMBER_MIN = 0;
    public static final int DEFAULT_NUMBER_MAX = Integer.MAX_VALUE;
    public static final int DEFAULT_ARRAY_ITEMS_MIN = 1;
    public static final int DEFAULT_ARRAY_ITEMS_MAX = 5;
    public static final Number DEFAULT_NUMBER_MULTIPLE_OF = 1;
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

    // for numbers
    private final Number numberMin;
    private final Number numberMax;

    // for array type
    private final Integer arrayItemsMin;
    private final Integer arrayItemsMax;

    // for schemas
    private final Map<Class<? extends Schema>, SchemaGenerator<? extends Schema>> schemaGenerators;

    // for string formats
    private final Map<String, Supplier<String>> stringFormatGenerators;

    // for java types
    private final Map<Class<?>, Supplier<?>> javaTypeGenerators;

    // for predefined property name
//    private final Map<Class<?>, Supplier<?>> predefinedGenerators;

    public Context(JsonGeneratorConfig config) {
        this.definitionsPath = config.getDefinitionsPath();
        this.isOnlyRequiredProps = config.isOnlyRequiredProps();

        // Date, Time and DateTime formats - must be defined before calling getDefaultStringFormatGenerators method
        this.dateFormatter = ofNullable(config.getDateFormatter()).orElse(DEFAULT_DATE_FORMATTER);
        this.dateFrom = ofNullable(config.getDateFrom()).orElse(DEFAULT_LOCAL_DATE_FROM);
        this.dateTo = ofNullable(config.getDateTo()).orElse(DEFAULT_LOCAL_DATE_TO);

        this.timeFormatter = ofNullable(config.getTimeFormatter()).orElse(DEFAULT_TIME_FORMATTER);
        this.timeFrom = ofNullable(config.getTimeFrom()).orElse(DEFAULT_LOCAL_TIME_FROM);
        this.timeTo = ofNullable(config.getTimeTo()).orElse(DEFAULT_LOCAL_TIME_TO);

        this.dateTimeFormatter = ofNullable(config.getDateTimeFormatter()).orElse(DEFAULT_DATE_TIME_FORMATTER);
        this.dateTimeFrom = ofNullable(config.getDateTimeFrom()).orElse(DEFAULT_LOCAL_DATE_TIME_FROM);
        this.dateTimeTo = ofNullable(config.getDateTimeTo()).orElse(DEFAULT_LOCAL_DATE_TIME_TO);

        // Duration - must be defined before calling getDefaultStringFormatGenerators method
       this.durationFrom = ofNullable(config.getDurationFrom()).orElse(DEFAULT_DURATION_FROM);
       this.durationTo = ofNullable(config.getDurationTo()).orElse(DEFAULT_DURATION_TO);

        // String Generator
        this.stringLengthMin = ofNullable(config.getStringLengthMin()).orElse(DEFAULT_STRING_LENGTH_MIN);
        this.stringLengthMax = ofNullable(config.getStringLengthMax()).orElse(DEFAULT_STRING_LENGTH_MAX);

        // Number Generator
        this.numberMin =  ofNullable(config.getNumberMin()).orElse(DEFAULT_NUMBER_MIN);
        this.numberMax = ofNullable(config.getNumberMax()).orElse(DEFAULT_NUMBER_MAX);

        // Array Generator
        this.arrayItemsMin = ofNullable(config.getArrayItemsMin()).orElse(DEFAULT_ARRAY_ITEMS_MIN);
        this.arrayItemsMax = ofNullable(config.getArrayItemsMax()).orElse(DEFAULT_ARRAY_ITEMS_MAX);

        // Schema generators
        this.schemaGenerators = getDefaultSchemaGenerators();

        // String format generators
        Map<String, Supplier<String>> formats = getDefaultStringFormatGenerators();
        formats.putAll(config.getStringFormatGenerators());
        this.stringFormatGenerators = Map.copyOf(formats);

        // Java type generators
        Map<Class<?>, Supplier<?>> javaTypes = getDefaultJavaTypeGenerators();
        javaTypes.putAll(config.getJavaTypeGenerators());
        this.javaTypeGenerators = Map.copyOf(javaTypes);

        THREAD_LOCAL.set(this);
    }

    public static Context context() {
        return THREAD_LOCAL.get();
    }

    @SuppressWarnings("unchecked")
    private Map<Class<? extends Schema>, SchemaGenerator<? extends Schema>> getDefaultSchemaGenerators() {
        Set<Class<? extends SchemaGenerator>> col = getClasses(SchemaGenerator.class);
        return col.stream()
                .filter(not(ReflectionUtils::isAbstractClass).and(not(Class::isInterface)))
                .map(c -> getDefaultInstanceOf(c, this))
                .collect(toMap(SchemaGenerator::schemaType, Function.identity()));
    }

    /**
     * Read the classes in the {@link com.fibanez.jsonschema.content.generator.stringFormat} package that implement the
     * interface {@link FormatGenerator}
     *
     * @return Collection of formats and their default generators
     */
    private Map<String, Supplier<String>> getDefaultStringFormatGenerators() {
        Set<Class<? extends FormatGenerator>> col = getClasses(FormatGenerator.class);

        return col.stream()
                .map(c -> getDefaultInstanceOf(c, this))
                .collect(toMap(FormatGenerator::format, Function.identity()));
    }

    /**
     * @return Collection of Java types and their default generators
     */
    @SuppressWarnings("unchecked")
    private Map<Class<?>, Supplier<?>> getDefaultJavaTypeGenerators() {
        Set<Class<? extends JavaTypeGenerator>> col = getClasses(JavaTypeGenerator.class);
        return col.stream()
                .filter(not(Class::isInterface))
                .map(c -> getDefaultInstanceOf(c, this))
                .collect(toMap(JavaTypeGenerator::classType, Function.identity()));
    }

    public static SchemaGenerator<? extends Schema> getSchemaGenerator(@NonNull Class<? extends Schema> schemaClass) {
        Map<Class<? extends Schema>, SchemaGenerator<? extends Schema>> schemaGenerators = context().getSchemaGenerators();
        if (schemaGenerators.containsKey(schemaClass)) {
            return schemaGenerators.get(schemaClass);
        }
        throw new GeneratorException("Schema generator not found for: " + schemaClass);
    }

    public static FormatGenerator getFormatGenerator(@NonNull String format) {
        Map<String, Supplier<String>> stringFormatGenerators = context().getStringFormatGenerators();
        if (stringFormatGenerators.containsKey(format)) {
            return (FormatGenerator) stringFormatGenerators.get(format);
        }
        throw new GeneratorException("String format generator not found for: " + format);
    }

    @SuppressWarnings("unchecked")
    public static <T> JavaTypeGenerator<T> getJavaTypeGenerator(@NonNull Class<T> type) {
        Map<Class<?>, Supplier<?>> javaTypeGenerators = context().getJavaTypeGenerators();
        if (javaTypeGenerators.containsKey(type)) {
            return (JavaTypeGenerator<T>) javaTypeGenerators.get(type);
        }
        throw new GeneratorException("Java type generator not found for: " + type);
    }

}
