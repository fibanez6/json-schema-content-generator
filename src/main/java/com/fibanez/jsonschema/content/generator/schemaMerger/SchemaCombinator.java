package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.ConditionalSchema;
import org.everit.json.schema.ConstSchema;
import org.everit.json.schema.EnumSchema;
import org.everit.json.schema.NotSchema;
import org.everit.json.schema.NullSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SchemaCombinator {

    private static final String ALL_OF = "allOf";
    private static final String ANY_OF = "anyOf";
    private static final String ONE_OF = "oneOf";

    private static final List<Class<? extends Schema>> enumPriority = Arrays.asList(
            EnumSchema.class,
            NotSchema.class,
            ConstSchema.class,
            StringSchema.class
    );


    /**
     * Schemas are not in the list have higher priority and same level, and
     * Schemas contained in the list, the priority goes from top to bottom
     */
    private static final Comparator<Schema> enumComparator = Comparator.comparingInt((Schema s) -> enumPriority.indexOf(s.getClass()));

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

        sortSchemas(schemas);

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
        } else if (ALL_OF.equals(criterion)) {
            return allOf(subSchemas);
        }

        return Collections.emptyList();
    }

    /**
     * Exactly one of the given subSchemas
     */
    private static List<Schema> oneOf(Collection<Schema> subSchemas) {
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
     * Any (one or more) of the given subSchemas.
     */
    private static List<Schema> anyOf(Collection<Schema> subSchemas) {
        Schema candidate = RandomUtils.nextElement(subSchemas);
        return Collections.singletonList(candidate);
    }

    /**
     * All the given subSchemas.
     * Preserves the schema order
     */
    private static List<Schema> allOf(Collection<Schema> subSchemas) {
        LinkedList<Schema> queueToProcess = new LinkedList<>(subSchemas);
        LinkedList<Schema> schemasToReturn = new LinkedList<>();

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

    private static void sortSchemas(List<Schema> schemas) {
        boolean containsInstance = schemas.stream().anyMatch(c -> c instanceof EnumSchema);
        if (containsInstance) {
            schemas.sort(enumComparator);
        }
    }
}
