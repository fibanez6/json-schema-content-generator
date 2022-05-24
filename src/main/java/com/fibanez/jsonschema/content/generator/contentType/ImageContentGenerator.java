package com.fibanez.jsonschema.content.generator.contentType;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import com.fibanez.jsonschema.content.generator.util.FileUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base16;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.BinaryCodec;

import java.lang.ref.SoftReference;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageContentGenerator implements ContentType {

    private static final Base32 base32 = new Base32();
    private static final Base16 base16 = new Base16();

    // TODO do this dynamically or move to Context
    private static final Map<String, String> IMAGE_MAP = Map.of(
            "*", "contentType/images/helloWorld.png",
            "png", "contentType/images/helloWorld.png",
            "jpeg", "contentType/images/helloWorld.jpeg"
    );

    // Lazy initialization
    private SoftReference<byte[]> pngRef;

    @Override
    public String get(String contentType, Encode encode) {
        Optional<String> filename = getImageFilename(contentType);
        if (filename.isEmpty()) {
            throw new GeneratorException("Image contentType not found for content type: %s", contentType);
        }

        byte[] data = getImageBytes(filename.get());
        switch (encode) {
            case BINARY:
                return BinaryCodec.toAsciiString(data);
            case BASE16:
                return base16.encodeAsString(data);
            case BASE32:
                return base32.encodeAsString(data);
            case BASE64:
            case DEFAULT:
                return Base64.getEncoder().encodeToString(data);
            default:
                throw new GeneratorException("No supported encode %s", encode.name());
        }
    }

    @Override
    public String get() {
        return get("png", Encode.BASE64);
    }

    @Override
    public String mimeType() {
        return MimeType.IMAGE.value();
    }

    private Optional<String> getImageFilename(String contentType) {
        String type = contentType.split(CONTENT_TYPE_SEPARATOR)[1];
        String filename = IMAGE_MAP.get(type);
        return Optional.ofNullable(filename);
    }

    private byte[] getImageBytes(String filename) {
        byte[] data = (pngRef != null) ? pngRef.get() : null;
        if (Objects.isNull(data) || data.length == 0) {
            data = FileUtils.getImageResource(filename);
            this.pngRef = new SoftReference<>(data);
        }
        return data;
    }
}
