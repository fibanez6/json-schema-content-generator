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
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.fibanez.jsonschema.content.generator.util.RandomUtils.between;

public final class ArraySchemaGenerator implements SchemaGenerator<ArraySchema> {

    private static final String DRAFT_201909_PREFIX_ITEMS = "prefixItems";

    @Override
    public Object generate(@NonNull ArraySchema schema, @NonNull CrumbPath crumbPath) {
        int totalItems = getTotalItems(schema);
        boolean requireUniqueItems = schema.needsUniqueItems();

        Optional<JSONArray> predefinedStringValue = getPredefinedArrayValues(crumbPath, totalItems, requireUniqueItems);
        if (predefinedStringValue.isPresent()) {
            return predefinedStringValue.get();
        }

        List<Schema> itemSchema = schema.getItemSchemas();
        if (itemSchema != null && !itemSchema.isEmpty()) {
            return generateFromItemSchemas(itemSchema, crumbPath, totalItems, requireUniqueItems);
        }
        Schema allItemSchema = schema.getAllItemSchema();
        if (Objects.nonNull(allItemSchema)) {
            return generateFromAllItemsSchema(allItemSchema, crumbPath, totalItems, requireUniqueItems); // TODO
        }

        // TODO
//        Map<String, Object> unprocessedProperties = schema.getUnprocessedProperties();


        return new JSONArray();
    }
    
    // TODO
    private Optional<JSONArray> getPredefinedArrayValues(CrumbPath crumbPath, int totalItems, boolean requireUniqueItems) {
//        Collector<Object, ?, JSONArray> collector = getCollector(requireUniqueItems);
//
//        IntStream.range(0, totalItems).mapToObj(idx -> {
//             Context.getFormatGenerator();
//        });
//
//
        return Optional.empty();
    }

//    private Optional<JSONArray> getPredefinedArrayValues(Integer maxItems, boolean requireUniqueVal) {
//        Collection<Object> values = initValueCollection(maxItems, requireUniqueVal);
//        JSONArray result = new JSONArray();
//        int cnt = 0;
//        for (Object je : config.getArrayPredefinedItems(propertyName)) {
//            Supplier supplier = getPredefinedPathValue(cnt).orElse(() -> je);
//            Object value = supplier.get();
//            values.add(value);
//            if (++cnt > maxItems)
//                break;
//        }
//        if (values.isEmpty()) {
//            return Optional.empty();
//        } else {
//            values.forEach(result::put);
//            return Optional.of(result);
//        }
//    }

    private JSONArray generateFromAllItemsSchema(Schema allItemSchema, CrumbPath crumbPath, int totalItems, boolean requireUniqueItems) {
        Collector<Object, ?, JSONArray> collector = getCollector(requireUniqueItems);
        return IntStream.range(0, totalItems)
                .mapToObj(idx -> {
                    CrumbPath nextCrumbPath = CrumbPath.getArrayNext(idx, crumbPath);
                    return generateFrom(allItemSchema, nextCrumbPath);
                })
                .collect(collector);
    }

    private JSONArray generateFromItemSchemas(List<Schema> items, CrumbPath crumbPath, int totalItems, boolean requireUniqueItems) {
        Collector<Object, ?, JSONArray> collector = getCollector(requireUniqueItems);
        return IntStream.range(0, totalItems)
                .mapToObj(idx -> {
                    Schema schema = RandomUtils.nextElement(items);
                    CrumbPath nextCrumbPath = CrumbPath.getArrayNext(idx, crumbPath);
                    return generateFrom(schema, nextCrumbPath);
                })
                .collect(collector);
    }

    private int getTotalItems(ArraySchema schema) {
        Context context = Context.context();
        int minItems = getOrDefault(schema.getMinItems(), context.getArrayItemsMin());
        int maxItems = getOrDefault(schema.getMaxItems(), context.getArrayItemsMax());
        return between(minItems, maxItems);
    }

    private Collector<Object, ?, JSONArray> getCollector(boolean requireUniqueItems) {
        Collector<Object, ?, ? extends Collection<Object>> collector = (requireUniqueItems)
                ? Collectors.toSet()
                : Collectors.toList();
        return Collectors.collectingAndThen(collector, JSONArray::new);
    }

    // TODO
//    private JSONArray generateFromAllItemsSchema(Schema schema, CrumbPath crumbPath, int totalItems, boolean requireUniqueItems) {
//        Collection<Object> colValues = getInitValueCollection(totalItems, requireUniqueItems);
//        int totalLoops = totalItems * 2;
//        while (totalLoops > 0 && colValues.size() < totalItems) {
//            CrumbPath nextCrumbPath = CrumbPath.getArrayNext(colValues.size(), crumbPath);
//            Object value = generateFrom(schema, nextCrumbPath);
//            colValues.add(value);
//            totalLoops--;
//        }
//        return new JSONArray(colValues);
//    }
//    private JSONArray generateFromItemSchemas(List<Schema> items, CrumbPath crumbPath, int totalItems, boolean requireUniqueItems) {
//        Collection<Object> colValues = getInitValueCollection(totalItems, requireUniqueItems);
//        int totalLoops = totalItems * 2;
//        while (totalLoops > 0 && colValues.size() < totalItems) {
//            Schema schema = RandomUtils.nextElement(items);
//            CrumbPath nextCrumbPath = CrumbPath.getArrayNext(colValues.size(), crumbPath);
//            Object value = generateFrom(schema, nextCrumbPath);
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
