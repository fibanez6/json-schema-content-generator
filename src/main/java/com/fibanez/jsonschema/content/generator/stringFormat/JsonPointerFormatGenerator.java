package com.fibanez.jsonschema.content.generator.stringFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class JsonPointerFormatGenerator implements FormatGenerator {

    @Override
    public String get() {
        throw new UnsupportedOperationException("not implemented: " + this.getClass().getCanonicalName());
    }

    @Override
    public String format() {
        return Format.JSON_POINTER.value();
    }
}
