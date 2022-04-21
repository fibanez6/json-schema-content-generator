package com.fibanez.jsonschema.content.generator.stringFormat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.andreinc.mockneat.unit.user.Emails;

import static com.fibanez.jsonschema.content.generator.stringFormat.FormatGenerator.Format.EMAIL;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class EmailFormatGenerator implements FormatGenerator {

    @Override
    public String get() {
        return Emails.emails().get();
    }

    @Override
    public String format() {
        return EMAIL.value();
    }
}
