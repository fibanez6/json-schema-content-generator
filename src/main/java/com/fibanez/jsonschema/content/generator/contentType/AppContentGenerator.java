package com.fibanez.jsonschema.content.generator.contentType;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import com.fibanez.jsonschema.content.generator.util.FileUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

// FIXME Duplicated code with ImageContentGenerator
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppContentGenerator implements ContentType {

    // TODO do this dynamically or move to Context
    private static final Map<String, String> APP_MAP = Map.of(
            "*", "contentType/application/helloWorld.pdf",
            "pdf", "contentType/application/helloWorld.pdf"
    );

    // Lazy initialization
    private SoftReference<byte[]> pngRef;

    @Override
    public String get(String contentType, Encode encode) {
        Optional<String> filename = getAppFilename(contentType);
        if (filename.isEmpty()) {
            throw new GeneratorException("Application contentType not found for content type: %s", contentType);
        }

        byte[] data = getAppBytes(filename.get());
        // TODO add more scenarios
        if (encode == Encode.DEFAULT || encode == Encode.UTF8) {
            return new String(data, StandardCharsets.UTF_8);
        }
        throw new GeneratorException("No supported encode %s", encode.name());
    }

    @Override
    public String get() {
        return get("pdf", Encode.UTF8);
    }

    @Override
    public String mimeType() {
        return MimeType.APPLICATION.value();
    }

    private Optional<String> getAppFilename(String contentType) {
        String type = contentType.split(CONTENT_TYPE_SEPARATOR)[1];
        String filename = APP_MAP.get(type);
        return Optional.ofNullable(filename);
    }

    private byte[] getAppBytes(String filename) {
        byte[] data = (pngRef != null) ? pngRef.get() : null;
        if (Objects.isNull(data) || data.length == 0) {
            data = FileUtils.getImageResource(filename);
            this.pngRef = new SoftReference<>(data);
        }
        return data;
    }

}
