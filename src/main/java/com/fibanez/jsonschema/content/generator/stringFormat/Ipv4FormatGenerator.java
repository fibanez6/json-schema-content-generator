package com.fibanez.jsonschema.content.generator.stringFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.andreinc.mockneat.unit.networking.IPv4s;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class Ipv4FormatGenerator implements FormatGenerator {

    @Override
    public String get() {
        return IPv4s.ipv4s().get();
    }

    @Override
    public String format() {
        return Format.IPV4.value();
    }
}
