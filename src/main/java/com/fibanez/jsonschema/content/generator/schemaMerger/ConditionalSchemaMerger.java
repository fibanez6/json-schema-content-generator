package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.builder.SchemaBuilder;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.Getter;
import lombok.NonNull;
import org.everit.json.schema.ConditionalSchema;
import org.everit.json.schema.Schema;

import java.util.Optional;

@Getter
public class ConditionalSchemaMerger implements SchemaMerger {

    private SchemaMerger schemaMerger;

    ConditionalSchemaMerger(ConditionalSchema schema) {
        this.schemaMerger = shake(schema);
    }

    @Override
    public Schema getSchema() {
        return schemaMerger.getSchema();
    }

    @Override
    public SchemaMerger combine(Schema schema) {
        if (schema instanceof ConditionalSchema) {
            SchemaMerger selected = shake((ConditionalSchema) schema);
            schemaMerger.combine(selected.getSchema());
        } else {
            schemaMerger.combine(schema);
        }
        return this;
    }

    @Override
    public SchemaMerger not(Schema schema) {
        if (schema instanceof ConditionalSchema) {
            SchemaMerger selected = shake((ConditionalSchema) schema);
            schemaMerger.not(selected.getSchema());
        } else {
            schemaMerger.not(schema);
        }
        return this;
    }

    private SchemaMerger shake(@NonNull ConditionalSchema schema) {
        Optional<Schema> ifSchema = schema.getIfSchema();
        Optional<Schema> thenSchema = schema.getThenSchema();
        Optional<Schema> elseSchema = schema.getElseSchema();

        SchemaMerger schemaMerger;
        if (elseSchema.isPresent()) {
            // 50% chance to go to if-then or else
            if (thenSchema.isPresent() && RandomUtils.nextBoolean()) {
                schemaMerger = SchemaMerger.forSchema(ifSchema.get()).combine(thenSchema.get());
            } else {
                Schema blank = SchemaBuilder.forSchema(ifSchema.get().getClass()).build();
                schemaMerger = SchemaMerger.forSchema(blank).not(ifSchema.get()).combine(elseSchema.get());
//                schemaMerger = SchemaMerger.forSchema(elseSchema.get()).not(ifSchema.get());
            }
        }
        // 50% chance to go to if-then or nothing
        else if (thenSchema.isPresent() && RandomUtils.nextBoolean()) {
            schemaMerger = SchemaMerger.forSchema(ifSchema.get()).combine(thenSchema.get());
        }
        // Negate if-then
        else {
            Schema blank = SchemaBuilder.forSchema(ifSchema.get().getClass()).build();
            schemaMerger = SchemaMerger.forSchema(blank).not(ifSchema.get());
        }
        return schemaMerger;
    }

}
