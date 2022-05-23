package com.fibanez.jsonschema.content.testUtil;

import com.fibanez.jsonschema.content.generator.contentMediaType.ContentMediaType;

public final class TestContentTypeGenerator implements ContentMediaType {

    @Override
    public String get() {
        return contentMediaType();
    }

    @Override
    public String get(Encode encode) {
        return contentMediaType();
    }

    @Override
    public String contentMediaType() {
        return "test-content";
    }
}
