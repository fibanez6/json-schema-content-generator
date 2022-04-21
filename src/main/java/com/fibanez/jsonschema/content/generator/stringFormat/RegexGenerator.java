package com.fibanez.jsonschema.content.generator.stringFormat;

import com.fibanez.jsonschema.content.generator.abstraction.SingleArgGenerator;
import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import com.mifmif.common.regex.Generex;
import lombok.NonNull;
import org.apache.commons.text.translate.UnicodeUnescaper;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public final class RegexGenerator implements FormatGenerator, SingleArgGenerator<String, String> {

    private static final UnicodeUnescaper UNICODE_UNESCAPER = new UnicodeUnescaper();

    @Override
    public String get() {
        throw new UnsupportedOperationException("Requires a pattern");
    }

    @Override
    public String get(@NonNull String pattern) {
        String sanitised = sanitizeRegex(pattern);
        String unidecode = UNICODE_UNESCAPER.translate(sanitised);
        Generex generex = new Generex(unidecode, RandomUtils.RANDOM);
        return generex.random();
    }

    /**
     * Generex doesn't like the start with '^', the end with '$' and '?:'
     */
    private String sanitizeRegex(String input) {
        try {
            final StringWriter writer = new StringWriter(input.length() * 2);
            sanitizeRegex(input, writer);
            return writer.toString();
        } catch (final IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            throw new GeneratorException("Error in sanitizing pattern", ioe);
        }
    }

    /**
     * Generex doesn't like '^', then '$' and '?:'
     */
    private void sanitizeRegex(final CharSequence input, final Writer out) throws IOException {
        int pos = 0;
        int len = input.length();
        while (pos < len) {
            if (isMatchEverythingEnclosed(input, pos)) {
                pos += 2;
                continue;
            }
            if (isStartOfString(input, pos) || isEndOfString(input, pos)) {
                pos++;
                continue;
            }
            if (shouldEscapeChar(input, pos)) {
                out.write("\\");
            }
            out.write(input.charAt(pos));
            pos++;
        }
    }

    /**
     * Match everything enclosed, but won't create a capture group
     * If pattern contains ?:
     */
    private boolean isMatchEverythingEnclosed(final CharSequence input, final int pos) {
        return input.charAt(pos) == '?' && pos < input.length() - 1 && input.charAt(pos + 1) == ':';
    }
    /**
     * If pattern starts with caret
     */
    private boolean isStartOfString(final CharSequence input, final int pos) {
        return (pos == 0 && input.charAt(pos) == '^')
                || (input.charAt(pos) == '^' && input.charAt(pos - 1) != '\\' && input.charAt(pos - 1) != '[');
    }
    /**
     * If pattern ends with dollar
     */
    private boolean isEndOfString(final CharSequence input, final int pos) {
        return (pos > 0 && input.charAt(pos) == '$' && input.charAt(pos - 1) != '\\');
    }
    /**
     * Generex doesn't like '<' so we have to escape them
     */
    private boolean shouldEscapeChar(final CharSequence input, final int pos) {
        return input.charAt(pos) == '<';
    }

    @Override
    public String format() {
        return Format.REGEX.value();
    }
}
