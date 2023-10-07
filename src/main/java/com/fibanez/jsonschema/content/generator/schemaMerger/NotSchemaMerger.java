package com.fibanez.jsonschema.content.generator.schemaMerger;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.everit.json.schema.ConstSchema;
import org.everit.json.schema.NotSchema;
import org.everit.json.schema.Schema;

import java.util.ArrayDeque;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class NotSchemaMerger implements SchemaMerger {

    private ArrayDeque<Schema> mustNotMatchQueue = new ArrayDeque<>();

    @Override
    public Schema getSchema() {
        return null;
    }

    @Override
    public SchemaMerger combine(Schema schema) {
        return process(schema);
    }

    @Override
    public SchemaMerger not(Schema schema) {
        return process(schema);
    }

    /**
     * Collect consecutive NotSchemas or ConstSchema schemas to process them later.
     */
    public SchemaMerger process(Schema schema) {
        if (schema instanceof NotSchema notSchema) {
            mustNotMatchQueue.add(notSchema.getMustNotMatch());
        } else if (mustNotMatchQueue.isEmpty() || schema instanceof ConstSchema) {
            mustNotMatchQueue.add(schema);
        } else {
            SchemaMerger schemaMerger = SchemaMerger.forSchema(schema).combine(schema);
            while (!mustNotMatchQueue.isEmpty()) {
                schemaMerger = schemaMerger.not(mustNotMatchQueue.poll());
            }
            return schemaMerger;
        }
        return this;
    }
}
