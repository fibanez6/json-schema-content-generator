package com.fibanez.jsonschema.content.generator.stringFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.andreinc.mockneat.unit.networking.URLs;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class UriFormatGenerator implements FormatGenerator {

    @Override
    public String get() {
        return URLs.urls().get();
    }

    @Override
    public String format() {
        return Format.URI.value();
    }
}
