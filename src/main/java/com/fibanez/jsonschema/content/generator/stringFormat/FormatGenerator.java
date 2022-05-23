package com.fibanez.jsonschema.content.generator.stringFormat;

import com.fibanez.jsonschema.content.generator.Generator;
import lombok.RequiredArgsConstructor;

public interface FormatGenerator extends Generator<String> {

    String format();

    @RequiredArgsConstructor
    enum Format {
        // Dates and times
        DATE_TIME("date-time"),
        DATE("date"),                   // draft 7
        TIME("time"),                   // draft 7
        DURATION("duration"),           // draft 2019-09

        // Email addresses
        EMAIL("email"),
        IDN_EMAIL("idn-email"),         // draft 7

        // Hostnames
        HOSTNAME("hostname"),
        IDN_HOSTNAME("idn-hostname"),   // draft 7

        // IP Addresses
        IPV4("ipv4"),
        IPV6("ipv6"),

        // Resource identifiers
        UUID("uuid"),                   // draft 2019-09
        URI("uri"),
        URI_REFERENCE("uri-reference"), // draft 6
        IRI("iri"),                     // draft 7
        IRI_REFERENCE("iri-reference"), // draft 7

        // URI template
        URI_TEMPLATE("uri-template"),   // draft 6

        // JSON Pointer
        JSON_POINTER("json-pointer"),   // draft 6
        RELATIVE_JSON_POINTER("relative-json-pointer"), // draft 7

        // Regular Expressions
        REGEX("regex");                 // draft 7

        private final String value;

        public String value() {
            return value;
        }
    }

}
