package com.fibanez.jsonschema.content.testUtil;

import com.fibanez.jsonschema.content.generator.stringFormat.FormatGenerator;

public final class TestFormatGenerator implements FormatGenerator {

    @Override
    public String get() {
        return format();
    }

    @Override
    public String format() {
        return "test-format";
    }
}
