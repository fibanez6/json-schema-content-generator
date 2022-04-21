package com.fibanez.jsonschema.content.generator.stringFormat;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.time.Duration;

import static com.fibanez.jsonschema.content.generator.stringFormat.FormatGenerator.Format.DURATION;

public final class DurationFormatGenerator implements FormatGenerator {

    private final Duration from;
    private final Duration to;

    private DurationFormatGenerator(Context context) {
        this(context.getDurationFrom(), context.getDurationTo());
    }

    public DurationFormatGenerator(@NonNull Duration from, @NonNull Duration to) {
        Validate.isTrue(to.compareTo(from) > 0, "TO Duration value must be before or equals to FROM Duration value");
        this.from = from;
        this.to = to;
    }

    @Override
    public String get() {
        Duration random = RandomUtils.between(from, to);
        return random.withNanos(0).toString();
    }

    @Override
    public String format() {
        return DURATION.value();
    }
}
