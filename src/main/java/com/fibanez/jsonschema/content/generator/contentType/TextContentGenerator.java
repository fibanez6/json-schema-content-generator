package com.fibanez.jsonschema.content.generator.contentType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TextContentGenerator implements ContentType {

    @Override
    public String get(String contentType, Encode encode) {
        // TODO
        throw new UnsupportedOperationException("no implemented");
    }

    @Override
    public String get() {
        return get("html", Encode.UTF8);
    }

    @Override
    public String mimeType() {
        return MimeType.TEXT.value();
    }

}
