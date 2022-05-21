package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.Context;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.everit.json.schema.ArraySchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.StringSchema;
import org.json.JSONArray;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.fibanez.jsonschema.content.generator.util.RandomUtils.between;
import static org.apache.commons.lang3.Validate.isTrue;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
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
        } else {
            Schema stringSchema = StringSchema.builder().build();
            return generateFromAllItemsSchema(stringSchema, jsonNode, totalItems, requireUniqueItems);
        }

        // TODO
//        Map<String, Object> unprocessedProperties = schema.getUnprocessedProperties();
//        return new JSONArray();
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
        int minItems = getMinItems(schema.getMinItems(), schema.getMaxItems());
        int maxItems = getMaxItems(minItems, schema.getMaxItems());
        isTrue(minItems >= 0, "Array items Min '{}' must be higher or equal to 0", minItems);
        return between(minItems, maxItems);
    }

    private Collector<Object, ?, JSONArray> getCollector(boolean requireUniqueItems) {
        Collector<Object, ?, ? extends Collection<Object>> collector = (requireUniqueItems)
                ? Collectors.toSet()
                : Collectors.toList();
        return Collectors.collectingAndThen(collector, JSONArray::new);
    }

    private int getMinItems(Integer minItems, Integer maxItems) {
        if (minItems != null) {
            return minItems;
        }
        Integer ctxMin = Context.current().getArrayItemsMin();
        if (maxItems != null && ctxMin > maxItems) {
            return 0;
        }
        return ctxMin;
    }

    private int getMaxItems(Integer minItems, Integer maxItems) {
        if (maxItems != null) {
            return maxItems;
        }
        Integer ctxMax = Context.current().getArrayItemsMax();
        if (minItems != null && minItems > ctxMax) {
            return minItems + Context.current().getArrayItemsMargin();
        }
        return ctxMax;
    }
}
