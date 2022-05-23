package com.fibanez.jsonschema.content.generator.contentMediaType;

import com.fibanez.jsonschema.content.generator.Generator;
import com.fibanez.jsonschema.content.generator.abstraction.SingleArgGenerator;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ContentMediaType extends Generator<String>, SingleArgGenerator<ContentMediaType.Encode, String> {

    String contentMediaType();

    @RequiredArgsConstructor
    enum ContentType {

        IMAGE_PNG("image/png");

        private final String value;

        public String value() {
            return value;
        }

    }

    @RequiredArgsConstructor
    enum Encode {
        UTF8("utf-8"),
        BITS7("7bit"),
        BITS8("8bit"),
        BINARY("binary"),
        QUOTED_PRINTABLE("quoted-printable"),
        BASE16("base16"),
        BASE32("base32"),
        BASE64("base64");

        private final String value;

        public String value() {
            return value;
        }

        private static Map<String, Encode> reverseLookup = Arrays.stream(values())
                .collect(Collectors.toMap(Encode::value, Function.identity()));

        public static Encode getByValue(final String value) {
            return reverseLookup.get(value);
        }

    }
}
