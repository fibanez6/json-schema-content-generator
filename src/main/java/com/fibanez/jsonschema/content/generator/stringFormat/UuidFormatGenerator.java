package com.fibanez.jsonschema.content.generator.stringFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class UuidFormatGenerator implements FormatGenerator {

    @Override
    public String get() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String format() {
        return Format.UUID.value();
    }
}
