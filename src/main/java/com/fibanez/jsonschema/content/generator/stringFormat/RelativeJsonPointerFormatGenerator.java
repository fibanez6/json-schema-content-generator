package com.fibanez.jsonschema.content.generator.stringFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class RelativeJsonPointerFormatGenerator implements FormatGenerator {

    @Override
    public String get() {
        return "0/";
    }

    @Override
    public String format() {
        return Format.RELATIVE_JSON_POINTER.value();
    }
}
