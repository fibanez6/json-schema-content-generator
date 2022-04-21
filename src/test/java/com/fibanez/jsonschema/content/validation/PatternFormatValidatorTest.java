package com.fibanez.jsonschema.content.validation;

import com.fibanez.jsonschema.content.validator.PatternFormatValidator;

public abstract class PatternFormatValidatorTest {

    private final String patternFormat;
    protected final PatternFormatValidator validator;

    public PatternFormatValidatorTest(String patternFormat, PatternFormatValidator validator) {
        this.patternFormat = patternFormat;
        this.validator = validator;
    }

    protected String getFormatErrorMsg(String invalidFormat) {
        return String.format("[%s] is not a valid %s. Expected [%s]", invalidFormat, validator.formatName(), patternFormat);
    }
}
