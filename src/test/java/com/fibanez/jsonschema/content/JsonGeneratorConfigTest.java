package com.fibanez.jsonschema.content;

import com.fibanez.jsonschema.content.generator.Generator;
import com.fibanez.jsonschema.content.generator.javaType.CollectionGenerator;
import com.fibanez.jsonschema.content.generator.javaType.ConstantGenerator;
import com.fibanez.jsonschema.content.generator.javaType.JavaTypeGenerator;
import com.fibanez.jsonschema.content.generator.javaType.StringGenerator;
import com.fibanez.jsonschema.content.testUtil.TestFormatGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
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
        assertTrue(config.getPredefinedValueGenerators().isEmpty());
        assertFalse(config.isOnlyRequiredProps());

        assertNull(config.getDateFormatter());
        assertNull(config.getDateFrom());
        assertNull(config.getDateTo());

        assertNull(config.getTimeFormatter());
        assertNull(config.getTimeFrom());
        assertNull(config.getTimeTo());

        assertNull(config.getDateTimeFormatter());
        assertNull(config.getDateTimeFrom());
        assertNull(config.getDateTimeTo());

        assertNull(config.getDurationFrom());
        assertNull(config.getDurationTo());

        assertNull(config.getStringLengthMax());
        assertNull(config.getStringLengthMin());
        assertNull(config.getNumberMin());
        assertNull(config.getNumberMax());
        assertNull(config.getArrayItemsMin());
        assertNull(config.getArrayItemsMax());
    }

    @Test
    void shouldReturnConfig_putFormatGenerators() {
        String format02 = FIXTURE.create(String.class);
        String format01 = FIXTURE.create(String.class);
        String format03 = FIXTURE.create(String.class);
        String format04 = FIXTURE.create(String.class);
        String format05 = FIXTURE.create(String.class);

        String constValue_01 = FIXTURE.create(String.class);
        String constValue_02 = FIXTURE.create(String.class);
        Collection<String> colValues = FIXTURE.collections().createCollection(String.class);

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .stringFormatGenerators(Map.of(format01, () -> FIXTURE.create(String.class)))
                .stringFormatGenerator(format02, new TestFormatGenerator())
                .stringFormatGenerator(format03, constValue_01)
                .stringFormatGenerator(format04, constValue_01, constValue_02)
                .stringFormatGenerator(format05, colValues)
                .build();

        assertInstanceOf(Generator.class, config.getFormatGenerator(format01));
        assertInstanceOf(TestFormatGenerator.class, config.getFormatGenerator(format02));
        assertInstanceOf(ConstantGenerator.class, config.getFormatGenerator(format03));
        assertInstanceOf(CollectionGenerator.class, config.getFormatGenerator(format04));
        assertInstanceOf(CollectionGenerator.class, config.getFormatGenerator(format05));
    }

    @Test
    void shouldReturnConfig_putJavaTypeGenerator() {
        Collection<Character> colValues = FIXTURE.collections().createCollection(Character.class);

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .javaTypeGenerators(Map.of(Integer.class, () -> FIXTURE.create(Integer.class)))
                .javaTypeGenerator(String.class, new StringGenerator())
                .javaTypeGenerator(Boolean.class, true)
                .javaTypeGenerator(Double.class, 1.0, 2.0, 3.0)
                .javaTypeGenerator(Character.class, colValues)
                .build();

        assertInstanceOf(Generator.class, config.getJavaTypeGenerator(Integer.class));
        assertInstanceOf(JavaTypeGenerator.class, config.getJavaTypeGenerator(String.class));
        assertInstanceOf(ConstantGenerator.class, config.getJavaTypeGenerator(Boolean.class));
        assertInstanceOf(CollectionGenerator.class, config.getJavaTypeGenerator(Double.class));
        assertInstanceOf(CollectionGenerator.class, config.getJavaTypeGenerator(Character.class));
    }

    @Test
    void shouldReturnConfig_putPredefinedValueGenerator() {
        String propertyName01 = FIXTURE.create(String.class);
        String propertyName02 = FIXTURE.create(String.class);
        String propertyName03 = FIXTURE.create(String.class);
        String propertyName04 = FIXTURE.create(String.class);
        String propertyName05 = FIXTURE.create(String.class);

        String constString = FIXTURE.create(String.class);
        Integer constInteger = FIXTURE.create(Integer.class);
        Collection colValues = Arrays.asList(constString, constInteger);

        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .predefinedValueGenerators(Map.of(propertyName01, () -> FIXTURE.create(Integer.class)))
                .predefinedValueGenerator(propertyName02, new StringGenerator())
                .predefinedValueGenerator(propertyName03, constString)
                .predefinedValueGenerator(propertyName04, constString, constInteger)
                .predefinedValueGenerator(propertyName05, colValues)
                .build();

        assertInstanceOf(Supplier.class, config.getPredefinedValueGenerators(propertyName01));
        assertInstanceOf(JavaTypeGenerator.class, config.getPredefinedValueGenerators(propertyName02));
        assertInstanceOf(ConstantGenerator.class, config.getPredefinedValueGenerators(propertyName03));
        assertInstanceOf(CollectionGenerator.class, config.getPredefinedValueGenerators(propertyName04));
        assertInstanceOf(CollectionGenerator.class, config.getPredefinedValueGenerators(propertyName05));
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

    @Test
    void shouldReturnConfig_onlyRequiredProps() {
        JsonGeneratorConfig config = JsonGeneratorConfig.builder()
                .onlyRequiredProps()
                .build();
        assertTrue(config.isOnlyRequiredProps());
    }
}