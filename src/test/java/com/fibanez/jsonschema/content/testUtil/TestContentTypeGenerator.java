package com.fibanez.jsonschema.content.testUtil;

import com.fibanez.jsonschema.content.generator.contentType.ContentType;

public final class TestContentTypeGenerator implements ContentType {

    @Override
    public String get() {
        return mimeType();
    }

    @Override
    public String get(String contentType, Encode encode) {
        return mimeType();
    }

    @Override
    public String mimeType() {
        return "test-content";
    }
}
