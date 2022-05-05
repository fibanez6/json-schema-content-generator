package com.fibanez.jsonschema.content.generator.builder;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import lombok.NonNull;
import org.everit.json.schema.ArraySchema;
import org.everit.json.schema.BooleanSchema;
import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.ConditionalSchema;
import org.everit.json.schema.ConstSchema;
import org.everit.json.schema.EmptySchema;
import org.everit.json.schema.EnumSchema;
import org.everit.json.schema.NotSchema;
import org.everit.json.schema.NullSchema;
import org.everit.json.schema.NumberSchema;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.ReferenceSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;

public final class SchemaBuilder {

    private SchemaBuilder() {
        // Do nothing
    }

    public static <S extends Schema> Schema.Builder forSchema(@NonNull final Class<S> clazz) {
        if (clazz.isAssignableFrom(StringSchema.class)) {
            return StringSchema.builder();
        } else if (clazz.isAssignableFrom(NumberSchema.class)) {
            return NumberSchema.builder();
        } else if (clazz.isAssignableFrom(BooleanSchema.class)) {
            return BooleanSchema.builder();
        } else if (clazz.isAssignableFrom(EnumSchema.class)) {
            return EnumSchema.builder();
        } else if (clazz.isAssignableFrom(ArraySchema.class)) {
            return ArraySchema.builder();
        } else if (clazz.isAssignableFrom(ObjectSchema.class)) {
            return ObjectSchema.builder();
        } else if (clazz.isAssignableFrom(ReferenceSchema.class)) {
            return ReferenceSchema.builder();
        } else if (clazz.isAssignableFrom(CombinedSchema.class)) {
            return CombinedSchema.builder();
        } else if (clazz.isAssignableFrom(EmptySchema.class)) {
            return EmptySchema.builder();
        } else if (clazz.isAssignableFrom(NullSchema.class)) {
            return NullSchema.builder();
        } else if (clazz.isAssignableFrom(NotSchema.class)) {
            return NotSchema.builder();
        } else if (clazz.isAssignableFrom(ConditionalSchema.class)) {
            return ConditionalSchema.builder();
        } else if (clazz.isAssignableFrom(ConstSchema.class)) {
            return ConstSchema.builder();
        }
        throw new GeneratorException("Unsupported schema '%s'" + clazz);
    }

}
