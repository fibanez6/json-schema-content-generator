package com.fibanez.jsonschema.content.generator.stringFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.fibanez.jsonschema.content.generator.stringFormat.FormatGenerator.Format.IDN_HOSTNAME;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class IdnHostnameFormatGenerator implements FormatGenerator {

    @Override
    public String get() {
        throw new UnsupportedOperationException("not implemented: " + this.getClass().getCanonicalName());
    }

    @Override
    public String format() {
        return IDN_HOSTNAME.value();
    }
}
