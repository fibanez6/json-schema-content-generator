package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.ConditionalSchema;
import org.everit.json.schema.NotSchema;
import org.everit.json.schema.NullSchema;
import org.everit.json.schema.Schema;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SchemaCombinator {

    private static final String ANY_OF = "anyOf";
    private static final String ONE_OF = "oneOf";

    public static Schema combine(CombinedSchema schema) {
        List<Schema> schemasToMerge = getSubSchemasFrom(schema);
        return combineAll(schemasToMerge);
    }

    public static Schema combine(List<Schema> schemas) {
        List<Schema> schemasToMerge = allOf(schemas);
        return combineAll(schemasToMerge);
    }

    private static Schema combineAll(List<Schema> schemas) {
        if (schemas.isEmpty()) {
            return NullSchema.builder().build();
        }

        SchemaMerger merger = SchemaMerger.forSchema(schemas.get(0));
        for (Schema subSchema : schemas) {
            if (subSchema instanceof NotSchema) {
                merger = merger.not(((NotSchema) subSchema).getMustNotMatch());
            } else {
                merger = merger.combine(subSchema);
            }
        }
        return merger.getSchema();
    }

    private static List<Schema> getSubSchemasFrom(CombinedSchema schema) {
        String criterion = schema.getCriterion().toString();
        Collection<Schema> subSchemas = schema.getSubschemas();

        if (subSchemas.isEmpty()) {
            return Collections.emptyList();
        }

        if (ONE_OF.equals(criterion)) {
            return oneOf(subSchemas);
        } else if (ANY_OF.equals(criterion)) {
            return anyOf(subSchemas);
        } else {
            // if all items in the collection are conditionals, then choose one randomly.
            boolean areAllConditionals = subSchemas.stream().allMatch(s -> s instanceof ConditionalSchema);
            if (areAllConditionals) {
                return oneOf(subSchemas);
            }
            return allOf(subSchemas);
        }
    }

    /**
     * Exactly one of the given subSchemas
     */
    private static List<Schema> oneOf(Collection<Schema> subSchemas) {
        List<Schema> schemasToReturn = new ArrayList<>();
        Schema selected = RandomUtils.nextElement(subSchemas);

        // Add selected schema and negate others
        schemasToReturn.add(selected);
        subSchemas.stream()
                .filter(Predicate.not(selected::equals))
                .map(s -> NotSchema.builder().mustNotMatch(s).build())
                .forEach(schemasToReturn::add);
        return schemasToReturn;
    }

    /**
     * Any (one or more) of the given subSchemas.
     */
    private static List<Schema> anyOf(Collection<Schema> subSchemas) {
        Schema selected = RandomUtils.nextElement(subSchemas);
        return List.of(selected);
    }

    /**
     * All the given subSchemas.
     */
    private static List<Schema> allOf(Collection<Schema> subSchemas) {
        ArrayDeque<Schema> queueToProcess = new ArrayDeque<>(subSchemas);
        List<Schema> schemasToReturn = new ArrayList<>();

        while (!queueToProcess.isEmpty()) {
            Schema subSchema = queueToProcess.poll();
            // Merge all schemas type of if-then-else and push back to the queue
            if (subSchema instanceof ConditionalSchema) {
                List<Schema> schemas = getSubSchemasFrom((ConditionalSchema) subSchema);
                queueToProcess.addAll(schemas);
                continue;
            }
            // Merge all schemas type of oneOf, anyOf and allOf and push back to the queue
            if (subSchema instanceof CombinedSchema) {
                List<Schema> schemas = getSubSchemasFrom((CombinedSchema) subSchema);
                queueToProcess.addAll(schemas);
                continue;
            }
            schemasToReturn.add(subSchema);
        }

        return schemasToReturn;
    }

    private static List<Schema> getSubSchemasFrom(ConditionalSchema schema) {
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
