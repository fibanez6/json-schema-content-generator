package com.fibanez.jsonschema.content.generator.contentMediaType;

public class ImagePngGenerator implements ContentMediaType {

    @Override
    public String get(Encode encode) {
        return null;
    }

    @Override
    public String get() {
        return null;
    }

    @Override
    public String contentMediaType() {
        return ContentType.IMAGE_PNG.value();
    }
}
