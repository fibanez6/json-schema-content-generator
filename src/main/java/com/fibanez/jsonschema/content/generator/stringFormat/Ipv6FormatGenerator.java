package com.fibanez.jsonschema.content.generator.stringFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.andreinc.mockneat.unit.networking.IPv6s;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class Ipv6FormatGenerator implements FormatGenerator {

    @Override
    public String get() {
        return IPv6s.ipv6s().get();
    }

    @Override
    public String format() {
        return Format.IPV6.value();
    }
}
