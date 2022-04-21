package com.fibanez.jsonschema.content.generator.stringFormat;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class DateTimeFormatGenerator implements FormatGenerator {

    private final LocalDateTime from;
    private final LocalDateTime to;
    private final DateTimeFormatter formatter;

    /**
     * Method only accessible by reflection in Context object
     */
    DateTimeFormatGenerator(Context context) {
        this(context.getDateTimeFrom(), context.getDateTimeTo(), context.getDateTimeFormatter());
    }

    public DateTimeFormatGenerator(@NonNull LocalDateTime from, @NonNull LocalDateTime to) {
        this(from, to, null);
    }

    public DateTimeFormatGenerator(@NonNull LocalDateTime from, @NonNull LocalDateTime to, DateTimeFormatter formatter) {
        Validate.isTrue(from.compareTo(to) <= 0, "TO localDateTime value must be before or equals to FROM localDateTime value");
        this.from = from;
        this.to = to;
        this.formatter = formatter;
    }

    @Override
    public String get() {
        DateTimeFormatter dtf = Objects.nonNull(formatter) ?  formatter :  Context.context().getDateTimeFormatter();
        LocalDateTime random = RandomUtils.between(from, to);
        return dtf.format(random.atOffset(ZoneOffset.UTC));
    }

    @Override
    public String format() {
        return Format.DATE_TIME.value();
    }
}
