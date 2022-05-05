package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.Getter;
import lombok.NonNull;
import org.everit.json.schema.ArraySchema;
import org.everit.json.schema.BooleanSchema;
import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.ConditionalSchema;
import org.everit.json.schema.ConstSchema;
import org.everit.json.schema.EnumSchema;
import org.everit.json.schema.NotSchema;
import org.everit.json.schema.NullSchema;
import org.everit.json.schema.NumberSchema;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.ReferenceSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Predicate;

@Getter
public class CombinedSchemaMerger implements SchemaMerger {

    private static final String ALL_OF = "allOf";
    private static final String ANY_OF = "anyOf";
    private static final String ONE_OF = "oneOf";

    private static final List<Class<? extends Schema>> orderSchemas = Arrays.asList(
            CombinedSchema.class,           // higher priority
            ConditionalSchema.class,
            NotSchema.class,
            ObjectSchema.class,
            ArraySchema.class,
            StringSchema.class,
            BooleanSchema.class,
            NumberSchema.class,
            EnumSchema.class,
            ReferenceSchema.class,
            ConstSchema.class,
            NumberSchema.class              // lower priority
    );

    private static final Comparator<Schema> schemaOrderComparator = Comparator.comparingInt((Schema s) -> orderSchemas.indexOf(s.getClass()));

    private Schema schema; // TODO final

    public CombinedSchemaMerger(CombinedSchema schema) {
        this.schema = shake(schema);
    }

    @Override
    public SchemaMerger combine(Schema schema) {
        if (schema instanceof CombinedSchema) {
            this.schema = shake((CombinedSchema) schema);
        }
//        return this; // TODO
        throw new GeneratorException("Schema merger does not support: " + schema);
    }

    @Override
    public SchemaMerger not(Schema schema) {
        return this;
    }

    private Schema shake(@NonNull CombinedSchema schema) {
        String criterion = schema.getCriterion().toString();
        Collection<Schema> subSchemas = schema.getSubschemas();

        if (subSchemas.isEmpty()) {
            return NullSchema.builder().build();
        }
        if (ONE_OF.equals(criterion)) {
            return oneOf(subSchemas);
        } else if (ANY_OF.equals(criterion)) {
            return anyOf(subSchemas);
        } else if (ALL_OF.equals(criterion)) {
            return allOf(subSchemas);
        }

        throw new GeneratorException("Unsupported criterion '%s'", criterion);
    }

    /**
     * Exactly one of the given subschemas
     */
    private Schema oneOf(Collection<Schema> subSchemas) {
        Schema candidate = RandomUtils.nextElement(subSchemas);
        SchemaMerger schemaMerger = SchemaMerger.forSchema(candidate);
        subSchemas.stream()
                .filter(Predicate.not(candidate::equals))
                .forEach(schemaMerger::not);
        return schemaMerger.getSchema();
    }

    /**
     * Any (one or more) of the given subschemas.
     */
    private Schema anyOf(Collection<Schema> subSchemas) {
        Schema candidate = RandomUtils.nextElement(subSchemas);
        SchemaMerger schemaMerger = SchemaMerger.forSchema(candidate);
        return schemaMerger.getSchema();
    }

    /**
     * All of the given subschemas.
     */
    private Schema allOf(Collection<Schema> subSchemas) {
        // Order by priority to merge schemas
        PriorityQueue<Schema> prioritySchemas = new PriorityQueue<>(subSchemas.size(), schemaOrderComparator);
        prioritySchemas.addAll(subSchemas);

        SchemaMerger schemaMerger = null;
        while (!prioritySchemas.isEmpty()) {
            Schema subSchema = prioritySchemas.poll();

            // Merge all schemas type oneOf, anyOf, allOf and if-then-else and push back to the queue
            if (subSchema instanceof CombinedSchema || subSchema instanceof ConditionalSchema) {
                SchemaMerger merger = SchemaMerger.forSchema(subSchema);
                prioritySchemas.offer(merger.getSchema());
                continue;
            }

            if (schemaMerger == null) {
                schemaMerger = SchemaMerger.forSchema(subSchema);
            } else {
                schemaMerger.combine(subSchema);
            }
        }
        return schemaMerger.getSchema();
    }
}
