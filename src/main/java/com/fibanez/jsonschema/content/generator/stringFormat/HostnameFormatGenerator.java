package com.fibanez.jsonschema.content.generator.stringFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.andreinc.mockneat.unit.networking.Domains;

import static com.fibanez.jsonschema.content.generator.stringFormat.FormatGenerator.Format.HOSTNAME;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class HostnameFormatGenerator implements FormatGenerator {

    @Override
    public String get() {
        return Domains.domains().get();
    }

    @Override
    public String format() {
        return HOSTNAME.value();
    }
}
