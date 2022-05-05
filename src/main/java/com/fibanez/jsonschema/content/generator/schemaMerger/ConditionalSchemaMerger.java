package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.Getter;
import lombok.NonNull;
import org.everit.json.schema.ConditionalSchema;
import org.everit.json.schema.NullSchema;
import org.everit.json.schema.Schema;

import java.util.Optional;

import static java.util.Objects.nonNull;

@Getter
public class ConditionalSchemaMerger implements SchemaMerger {

    private final Schema.Builder<?> schemaBuilder;

    ConditionalSchemaMerger(ConditionalSchema schema) {
        this.schemaBuilder = getSchemaBuilder(schema);
    }

    @Override
    public SchemaMerger combine(Schema schema) {
        throw new UnsupportedOperationException("no implemented");
    }

    @Override
    public SchemaMerger not(Schema schema) {
        throw new UnsupportedOperationException("no implemented");
    }

    @Override
    public Schema getSchema() {
        if (nonNull(schemaBuilder)) {
            return schemaBuilder.build();
        }
        return NullSchema.builder().build();
    }

    private Schema.Builder<?> getSchemaBuilder(@NonNull ConditionalSchema schema) {
        Optional<Schema> ifSchema = schema.getIfSchema();
        Optional<Schema> thenSchema = schema.getThenSchema();
        Optional<Schema> elseSchema = schema.getElseSchema();

        if (!ifSchema.isPresent()) {
            return NullSchema.builder();
        }

        SchemaMerger schemaMerger;
        // 50% chance to go to if-then or else
        if (elseSchema.isPresent() && RandomUtils.nextBoolean()) {
            schemaMerger = SchemaMerger.forSchema(elseSchema.get())
                    .not(ifSchema.get());
        } else if (thenSchema.isPresent()) {
            schemaMerger = SchemaMerger.forSchema(ifSchema.get())
                    .combine(thenSchema.get());
        } else {
            schemaMerger = SchemaMerger.forSchema(ifSchema.get());
        }
        return schemaMerger.getSchemaBuilder();
    }

}
