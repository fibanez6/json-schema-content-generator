package com.fibanez.jsonschema.content.generator.stringFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class JsonPointerFormatGenerator implements FormatGenerator {

    @Override
    public String get() {
        return "/";
    }

    @Override
    public String format() {
        return Format.JSON_POINTER.value();
    }
}
