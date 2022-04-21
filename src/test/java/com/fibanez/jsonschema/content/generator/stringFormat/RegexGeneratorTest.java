package com.fibanez.jsonschema.content.generator.stringFormat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegexGeneratorTest {

    private final RegexGenerator generator = new RegexGenerator();

    @Test
    void shouldThrowException_get() {
        assertThrows(UnsupportedOperationException.class, generator::get);
    }

    @Test
    void shouldThrowException_nullPattern() {
        assertThrows(NullPointerException.class, () -> generator.get(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "^\\d{2}(\\d{2}|XX)(\\d{2}|XX)$", // dateXX
            "^([0-9]{4})-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z$", // with escape characters
            "^[A-Za-zÀ-ÖØ-öø-İĲ-ķĹ-ňŊ-žƀ-ƃƇ-ƈƊ-ƌƑ-ƓƗ-ƚƝ-ơƤ-ƥƫ-ưƲ-ƶǍ-ǜǞ-ǡǤ-ǭǰ-ǰǴ-ǵǸ-ǻǾ-țȞ-ȡȤ-ȶȺ-ɀɃ-ɃɆ-ɉɋ-ɏḀ-ẚẠ-ỹỾ-ỿ]" +
                    "[-', A-Za-zÀ-ÖØ-öø-İĲ-ķĹ-ňŊ-žƀ-ƃƇ-ƈƊ-ƌƑ-ƓƗ-ƚƝ-ơƤ-ƥƫ-ưƲ-ƶǍ-ǜǞ-ǡǤ-ǭǰ-ǰǴ-ǵǸ-ǻǾ-țȞ-ȡȤ-ȶȺ-ɀɃ-ɃɆ-ɉɋ-ɏḀ-ẚẠ-ỹỾ-ỿ]*$$", // with special chars
            "^D<<$", // Generex doesn't like start and end of strings. So we have to replace them
            "(^D<AAA$|^C<BBB$)", // with start and end chars between parenthesis
            "^text:[0-9a-f]{3}-(?:[0-9a-f]{4}-){3}$", // Contains ?: - Match everything enclosed, but won't create a capture group
            "^[\\x21-\\x7E]{10,100}$" // with Hex code
    })
    void shouldReturnValidString_withPattern(String pattern) {
        String result = generator.get(pattern);
        assertThat(result, is(not(isEmptyString())));
        assertThat(result, matchesPattern(pattern));
    }

    @Test
    void shouldReturnValidString_withUnicodeEncoding() {
        String pattern = "\u0048\u0065\u006C\u006C\u006F World";
        String result = generator.get(pattern);

        assertEquals("Hello World", result);
        assertThat(result, matchesPattern(pattern));
    }
}