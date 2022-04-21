package com.fibanez.jsonschema.content;

import com.fibanez.jsonschema.content.generator.javaType.JavaTypeGenerator;
import com.fibanez.jsonschema.content.generator.javaType.StringGenerator;
import com.fibanez.jsonschema.content.testUtil.TestFormatGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Supplier;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.FIXTURE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonGeneratorConfigTest {

    @Test
    void shouldReturnConfig_defaults() {
        JsonGeneratorConfig config = JsonGeneratorConfig.builder().build();
        assertNotNull(config);

        assertNull(config.getDefinitionsPath());
        assertTrue(config.getJavaTypeGenerators().isEmpty());
        assertTrue(config.getStringFormatGenerators().isEmpty());
        assertFalse(config.isOnlyRequiredProps());

        assertNull(config.getDateFormatter());
        assertNull(config.getTimeFormatter());
        assertNull(config.getDateTimeFormatter());

        assertNull(config.getStringLengthMax());
        assertNull(config.getStringLengthMin());
    }

    @Test
    void shouldReturnConfig_putFormatGenerators() {
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .stringFormatGenerator("format01", new TestFormatGenerator())
                .stringFormatGenerator(Map.of("format02", () -> FIXTURE.create(String.class)))
                .build();

        assertInstanceOf(TestFormatGenerator.class, config.getFormatGenerator("format01"));
        assertInstanceOf(Supplier.class, config.getFormatGenerator("format02"));
    }

    @Test
    void shouldReturnConfig_putJavaTypeGenerator() {
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .javaTypeGenerator(String.class, new StringGenerator())
                .javaTypeGenerator(Map.of(Integer.class, () -> FIXTURE.create(Integer.class)))
                .build();

        assertInstanceOf(JavaTypeGenerator.class, config.getJavaTypeGenerator(String.class));
        assertInstanceOf(Supplier.class, config.getJavaTypeGenerator(Integer.class));
    }

    @Test
    void shouldReturnConfig_dateFormatPattern() {
        String pattern = "yyyy/MM/dd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate now = LocalDate.now();
        String expected = formatter.format(now);

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .dateFormatter(pattern)
                .build();

        String result = config.getDateFormatter().format(now);
        assertEquals(expected, result);
    }

    @Test
    void shouldReturnConfig_timeFormatPattern() {
        String pattern = "HH-mm-ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalTime now = LocalTime.now();
        String expected = formatter.format(now);

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .timeFormatter(pattern)
                .build();

        String result = config.getTimeFormatter().format(now);
        assertEquals(expected, result);
    }

    @Test
    void shouldReturnConfig_dateTimeFormatPattern() {
        String pattern = "yyyy/MM/dd'T'HH-mm-ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String expected = formatter.format(now);

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .dateTimeFormatter(pattern)
                .build();

        String result = config.getDateTimeFormatter().format(now);
        assertEquals(expected, result);
    }
}