package com.fibanez.jsonschema.content.generator.stringFormat;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class TimeFormatGenerator implements FormatGenerator {

    private final LocalTime from;
    private final LocalTime to;
    private final DateTimeFormatter formatter;

    /**
     * Method only accessible by reflection in Context object
     */
    TimeFormatGenerator(Context context) {
        this(context.getTimeFrom(), context.getTimeTo(), context.getTimeFormatter());
    }

    public TimeFormatGenerator(@NonNull LocalTime from, @NonNull LocalTime to) {
        this(from, to, null);
    }

    public TimeFormatGenerator(@NonNull LocalTime from, @NonNull LocalTime to, DateTimeFormatter formatter) {
        Validate.isTrue(from.compareTo(to) <= 0, "TO localTime value must be before or equals to FROM localTime value");
        this.from = from;
        this.to = to;
        this.formatter = formatter;
    }

    @Override
    public String get() {
        DateTimeFormatter dtf = Objects.nonNull(formatter) ? formatter : Context.current().getTimeFormatter();
        LocalTime random = RandomUtils.between(from, to);
        return dtf.format(random);
    }

    @Override
    public String format() {
        return Format.TIME.value();
    }
}
