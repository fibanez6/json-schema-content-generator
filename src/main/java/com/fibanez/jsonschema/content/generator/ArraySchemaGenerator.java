package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.NonNull;
import org.everit.json.schema.ArraySchema;
import org.everit.json.schema.Schema;
import org.json.JSONArray;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.fibanez.jsonschema.content.generator.util.RandomUtils.between;
import static org.apache.commons.lang3.Validate.isTrue;

public final class ArraySchemaGenerator implements SchemaGenerator<ArraySchema> {

    private static final String DRAFT_201909_PREFIX_ITEMS = "prefixItems";

    @Override
    public JSONArray generate(@NonNull ArraySchema schema, @NonNull JsonNode jsonNode) {
        int totalItems = getTotalItems(schema);
        boolean requireUniqueItems = schema.needsUniqueItems();

        List<Schema> itemSchema = schema.getItemSchemas();
        if (itemSchema != null && !itemSchema.isEmpty()) {
            return generateFromItemSchemas(itemSchema, jsonNode, totalItems, requireUniqueItems);
        }
        Schema allItemSchema = schema.getAllItemSchema();
        if (Objects.nonNull(allItemSchema)) {
            return generateFromAllItemsSchema(allItemSchema, jsonNode, totalItems, requireUniqueItems);
        }

        // TODO
//        Map<String, Object> unprocessedProperties = schema.getUnprocessedProperties();


        return new JSONArray();
    }

    private JSONArray generateFromAllItemsSchema(Schema allItemSchema, JsonNode jsonNode, int totalItems, boolean requireUniqueItems) {
        Collector<Object, ?, JSONArray> collector = getCollector(requireUniqueItems);
        return IntStream.range(0, totalItems)
                .mapToObj(idx -> {
                    JsonNode nextJsonNode = jsonNode.getArrayNext(idx);
                    return generateFrom(allItemSchema, nextJsonNode);
                })
                .collect(collector);
    }

    private JSONArray generateFromItemSchemas(List<Schema> items, JsonNode jsonNode, int totalItems, boolean requireUniqueItems) {
        Collector<Object, ?, JSONArray> collector = getCollector(requireUniqueItems);
        return IntStream.range(0, totalItems)
                .mapToObj(idx -> {
                    Schema schema = RandomUtils.nextElement(items);
                    JsonNode nextJsonNode = jsonNode.getArrayNext(idx);
                    return generateFrom(schema, nextJsonNode);
                })
                .collect(collector);
    }

    private int getTotalItems(ArraySchema schema) {
        Context context = Context.current();
        int minItems = getOrDefault(schema.getMinItems(), context.getArrayItemsMin());
        int maxItems = getOrDefault(schema.getMaxItems(), context.getArrayItemsMax());
        isTrue(minItems >= 0, "Array items Min '{}' must be higher or equal to 0", minItems);
        return between(minItems, maxItems);
    }

    private Collector<Object, ?, JSONArray> getCollector(boolean requireUniqueItems) {
        Collector<Object, ?, ? extends Collection<Object>> collector = (requireUniqueItems)
                ? Collectors.toSet()
                : Collectors.toList();
        return Collectors.collectingAndThen(collector, JSONArray::new);
    }

    // TODO
//    private JSONArray generateFromAllItemsSchema(Schema schema, JsonNode jsonNode, int totalItems, boolean requireUniqueItems) {
//        Collection<Object> colValues = getInitValueCollection(totalItems, requireUniqueItems);
//        int totalLoops = totalItems * 2;
//        while (totalLoops > 0 && colValues.size() < totalItems) {
//            JsonNode nextJsonNode = JsonNode.getArrayNext(colValues.size(), jsonNode);
//            Object value = generateFrom(schema, nextJsonNode);
//            colValues.add(value);
//            totalLoops--;
//        }
//        return new JSONArray(colValues);
//    }
//    private JSONArray generateFromItemSchemas(List<Schema> items, JsonNode jsonNode, int totalItems, boolean requireUniqueItems) {
//        Collection<Object> colValues = getInitValueCollection(totalItems, requireUniqueItems);
//        int totalLoops = totalItems * 2;
//        while (totalLoops > 0 && colValues.size() < totalItems) {
//            Schema schema = RandomUtils.nextElement(items);
//            JsonNode nextJsonNode = JsonNode.getArrayNext(colValues.size(), jsonNode);
//            Object value = generateFrom(schema, nextJsonNode);
//            colValues.add(value);
//            totalLoops--;
//        }
//        return new JSONArray(colValues);
//    }
//
//    private Collection<Object> getInitValueCollection(int size, boolean requireUniqueItems) {
//        return (requireUniqueItems) ? new HashSet<>(size) : new ArrayList<>(size);
//    }

}
