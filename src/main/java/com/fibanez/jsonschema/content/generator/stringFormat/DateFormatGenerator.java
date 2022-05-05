package com.fibanez.jsonschema.content.generator.stringFormat;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class DateFormatGenerator implements FormatGenerator {

    private final LocalDate from;
    private final LocalDate to;
    private final DateTimeFormatter formatter;

    /**
     * Method only accessible by reflection in Context object
     */
    DateFormatGenerator(Context context) {
        this(context.getDateFrom(), context.getDateTo(), context.getDateFormatter());
    }

    public DateFormatGenerator(@NonNull LocalDate from, @NonNull LocalDate to) {
        this(from, to, null);
    }

    public DateFormatGenerator(@NonNull LocalDate from, @NonNull LocalDate to, DateTimeFormatter formatter) {
        Validate.isTrue(from.compareTo(to) <= 0, "TO localDate value must be before or equals to FROM localDate value");
        this.from = from;
        this.to = to;
        this.formatter = formatter;
    }

    @Override
    public String get() {
        DateTimeFormatter dtf = Objects.nonNull(formatter) ? formatter : Context.current().getDateFormatter();
        LocalDate random = RandomUtils.between(from, to);
        return dtf.format(random);
    }

    @Override
    public String format() {
        return Format.DATE.value();
    }
}
