package com.fibanez.jsonschema.content.generator.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.Validate.isTrue;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomUtils {

    public static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static LocalDateTime between(@NonNull LocalDateTime from, @NonNull LocalDateTime to) {
        isTrue(from.compareTo(to) <= 0, "TO localDateTime value must be before or equals to FROM localDateTime value");
        long fromEpochSecond = from.toInstant(ZoneOffset.UTC).getEpochSecond();
        long toEpochSecond = to.toInstant(ZoneOffset.UTC).getEpochSecond();
        long randomEpochSecond = between(fromEpochSecond, toEpochSecond);
        return LocalDateTime.ofEpochSecond(randomEpochSecond, 0, ZoneOffset.UTC);
    }

    public static LocalDate between(@NonNull LocalDate from, @NonNull LocalDate to) {
        isTrue(from.compareTo(to) <= 0, "TO localDate value must be before or equals to FROM localDate value");
        long randomEpochDay = between(from.toEpochDay(), to.toEpochDay());
        return LocalDate.ofEpochDay(randomEpochDay);
    }

    public static LocalTime between(@NonNull LocalTime from, @NonNull LocalTime to) {
        isTrue(from.compareTo(to) <= 0, "TO localTime value must be before or equals to FROM localTime value");
        int randomSecondOfDay = between(from.toSecondOfDay(), to.toSecondOfDay());
        return LocalTime.ofSecondOfDay(randomSecondOfDay);
    }

    public static Duration between(@NonNull Duration from, @NonNull Duration to) {
        isTrue(to.compareTo(from) > 0, "TO Duration value must be before or equals to FROM Duration value");
        long randomMillis = between(from.toMillis(), to.toMillis());
        return Duration.ofMillis(randomMillis);
    }

    public static long between(long origin, long bound) {
        isTrue(origin <= bound, "Origin '{}' must be smaller or equal to bound '{}'", origin, bound);
        if (origin == bound) {
            return origin;
        }
        return origin + RANDOM.nextLong((bound - origin));
    }

    public static int between(int origin, int bound) {
        isTrue(origin <= bound, "Origin '{}' must be smaller or equal to bound '{}'", origin, bound);
        if (origin == bound) {
            return origin;
        }
        return origin + RANDOM.nextInt(bound - origin);
    }

    public static int nextInt(int bound) {
        if (bound == 0) {
            return 0;
        } else if (bound < 0) {
            return bound + RANDOM.nextInt(Math.abs(bound));
        }
        return RANDOM.nextInt(bound);
    }

    public static String string(int minLength, int maxLength) {
        isTrue(minLength >= 0, "Minimum length cannot be a negative value");
        isTrue(minLength <= maxLength, "Minimum length '{}' must be smaller or equal to Maximum length '{}'", minLength, maxLength);
        int cnt = between(minLength, maxLength);
        return RandomStringUtils.randomAlphabetic(cnt);
    }

    public static <E> E nextElement(Collection<E> collection) {
        int rdn = nextInt(collection.size());
        return collection.stream()
                .skip(rdn)
                .findFirst()
                .orElse(null);
    }

    public static boolean nextBoolean() {
        return RANDOM.nextBoolean();
    }
}
