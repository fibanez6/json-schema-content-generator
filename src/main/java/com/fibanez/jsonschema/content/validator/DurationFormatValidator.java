package com.fibanez.jsonschema.content.validator;

import com.fibanez.jsonschema.content.generator.stringFormat.FormatGenerator.Format;

import java.util.regex.Pattern;

/**
 * Validates the duration 'rfc3339: ISO 8601 Collected ABNF' format.
 *
 * P(n)Y(n)M(n)DT(n)H(n)M(n)S or P(n)W
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/draft-handrews-json-schema-validation-02#section-7.3.1">
 * JSON Schema Validation: 7.3.1. Dates, Times, and Duration
 * </a>
 *
 * The duration format is from the ISO 8601 ABNF as given in
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc3339#appendix-A">Appendix A of RFC 3339</a>
 *
 * Durations:
 *
 * dur-second = 1*DIGIT "S"
 * dur-minute = 1*DIGIT "M" [dur-second]
 * dur-hour = 1*DIGIT "H" [dur-minute]
 * dur-time = "T" (dur-hour / dur-minute / dur-second)
 * dur-day = 1*DIGIT "D"
 * dur-week = 1*DIGIT "W"
 * dur-month = 1*DIGIT "M" [dur-day]
 * dur-year = 1*DIGIT "Y" [dur-month]
 * dur-date = (dur-day / dur-month / dur-year) [dur-time]
 *
 * duration = "P" (dur-date / dur-time / dur-week)
 *
 * Samples:
 * PT0S, PT36H, P5Y, P1DT12H
 *
 */
public class DurationFormatValidator extends PatternFormatValidator {

    private static final String DUR_SECOND = "\\d+S";
    private static final String DUR_MINUTE = "\\d+M(?:" + DUR_SECOND + ")?";
    private static final String DUR_HOUR = "\\d+H(?:" + DUR_MINUTE + "|(?:" + DUR_SECOND + ")?)?";
    private static final String DUR_TIME = "T(?:" + DUR_HOUR + "|" + DUR_MINUTE + "|" + DUR_SECOND + ")";

    private static final String DUR_DAY = "\\d+D";
    private static final String DUR_WEEK = "\\d+W";
    private static final String DUR_MONTH = "\\d+M(?:" + DUR_DAY + ")?";
    private static final String DUR_YEAR = "\\d+Y(?:" + DUR_MONTH + ")?";
    private static final String DUR_DATE = "(?:" + DUR_DAY + "|" + DUR_MONTH + "|" + DUR_YEAR + ")(?:" + DUR_TIME + ")?";

    private static final Pattern DURATION = Pattern.compile("^P(?:" + DUR_DATE + "|" + DUR_TIME + "|" + DUR_WEEK + ")$");

    public DurationFormatValidator() {
        super(DURATION);
    }

    @Override
    public String formatName() {
        return Format.DURATION.value();
    }
}
