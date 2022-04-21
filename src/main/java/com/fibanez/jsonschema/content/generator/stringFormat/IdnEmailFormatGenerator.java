package com.fibanez.jsonschema.content.generator.stringFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.fibanez.jsonschema.content.generator.stringFormat.FormatGenerator.Format.IDN_EMAIL;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class IdnEmailFormatGenerator implements FormatGenerator {

    @Override
    public String get() {
        throw new UnsupportedOperationException("not implemented: " + this.getClass().getCanonicalName());
    }

    @Override
    public String format() {
        return IDN_EMAIL.value();
    }
}
