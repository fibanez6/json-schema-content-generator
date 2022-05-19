package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.generator.schemaMerger.SchemaMerger;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.NonNull;
import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.ConditionalSchema;
import org.everit.json.schema.NotSchema;
import org.everit.json.schema.NullSchema;
import org.everit.json.schema.Schema;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Predicate;

public final class CombinedSchemaGenerator implements SchemaGenerator<CombinedSchema> {

    private static final String ALL_OF = "allOf";
    private static final String ANY_OF = "anyOf";
    private static final String ONE_OF = "oneOf";

    private static final List<Class<? extends Schema>> orderSchemas = Arrays.asList(
            ConditionalSchema.class,
            CombinedSchema.class
    );

    /**
     * Schemas are not in the list have higher priority and same level, and
     * Schemas contained in the list, the priority goes from top to bottom
     */
    private static final Comparator<Schema> schemaOrderComparator = Comparator.comparingInt((Schema s) -> orderSchemas.indexOf(s.getClass()));


    @Override
    public Object generate(@NonNull CombinedSchema schema, @NonNull JsonNode jsonNode) {
        List<Schema> schemasToMerge = getSubSchemasFrom(schema);
        if (schemasToMerge.isEmpty()) {
            return NullSchema.builder().build();
        }

        SchemaMerger merger = SchemaMerger.forSchema(schemasToMerge.get(0));
        schemasToMerge.forEach(subSchema -> {
            if (subSchema instanceof NotSchema) {
                merger.not(((NotSchema) subSchema).getMustNotMatch());
            } else {
                merger.combine(subSchema);
            }
        });

        return generateFrom(merger.getSchema(), jsonNode);
    }

    private List<Schema> getSubSchemasFrom(CombinedSchema schema) {
        String criterion = schema.getCriterion().toString();
        Collection<Schema> subSchemas = schema.getSubschemas();

        if (subSchemas.isEmpty()) {
            return Collections.emptyList();
        }

        if (ONE_OF.equals(criterion)) {
            return oneOf(subSchemas);
        } else if (ANY_OF.equals(criterion)) {
            return anyOf(subSchemas);
        } else if (ALL_OF.equals(criterion)) {
            return allOf(subSchemas);
        }

        return Collections.emptyList();
    }

    /**
     * Exactly one of the given subschemas
     */
    private List<Schema> oneOf(Collection<Schema> subSchemas) {
        LinkedList<Schema> orderedSchemasToProcess = new LinkedList<>();
        Schema candidate = RandomUtils.nextElement(subSchemas);
        orderedSchemasToProcess.add(candidate);

        subSchemas.stream()
                .filter(Predicate.not(candidate::equals))
                .map(s -> NotSchema.builder().mustNotMatch(s).build())
                .forEach(orderedSchemasToProcess::add);
        return orderedSchemasToProcess;
    }

    /**
     * Any (one or more) of the given subschemas.
     */
    private List<Schema> anyOf(Collection<Schema> subSchemas) {
        Schema candidate = RandomUtils.nextElement(subSchemas);
        return Collections.singletonList(candidate);
    }

    /**
     * All of the given subschemas.
     */
    private List<Schema> allOf(Collection<Schema> subSchemas) {
        // Order by priority to merge schemas
        PriorityQueue<Schema> prioritySchemas = new PriorityQueue<>(subSchemas.size(), schemaOrderComparator);
        prioritySchemas.addAll(subSchemas);

        LinkedList<Schema> orderedSchemasToProcess = new LinkedList<>();
        while (!prioritySchemas.isEmpty()) {
            Schema subSchema = prioritySchemas.poll();
            // Merge all schemas type oneOf, anyOf, allOf and if-then-else and push back to the queue
            if (subSchema instanceof ConditionalSchema) {
                List<Schema> schemas = getSubSchemasFrom((ConditionalSchema) subSchema);
                schemas.forEach(prioritySchemas::offer);
                continue;
            }
            if (subSchema instanceof CombinedSchema) {
                List<Schema> schemas = getSubSchemasFrom((CombinedSchema) subSchema);
                schemas.forEach(prioritySchemas::offer);
                continue;
            }
            orderedSchemasToProcess.add(subSchema);
        }

        return orderedSchemasToProcess;
    }

    private List<Schema> getSubSchemasFrom(ConditionalSchema schema) {
        Optional<Schema> ifSchema = schema.getIfSchema();
        Optional<Schema> thenSchema = schema.getThenSchema();
        Optional<Schema> elseSchema = schema.getElseSchema();

        if (elseSchema.isPresent()) {
            // 50% chance to go to if-then or else
            if (thenSchema.isPresent() && RandomUtils.nextBoolean()) {
                return List.of(ifSchema.get(), thenSchema.get());
            } else {
                NotSchema negatedIf = NotSchema.builder().mustNotMatch(ifSchema.get()).build();
                return List.of(negatedIf, elseSchema.get());
            }
        }
        // 50% chance to go to if-then or nothing
        else if (thenSchema.isPresent() && RandomUtils.nextBoolean()) {
            return List.of(ifSchema.get(), thenSchema.get());
        }
        // Negate if-then
        else {
            NotSchema negatedIf = NotSchema.builder().mustNotMatch(ifSchema.get()).build();
            return List.of(negatedIf);
        }
    }

}
