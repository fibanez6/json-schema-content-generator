package com.fibanez.jsonschema.content.generator;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import com.fibanez.jsonschema.content.generator.util.RandomUtils;
import lombok.NonNull;
import org.everit.json.schema.CombinedSchema;
import org.everit.json.schema.Schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

@Deprecated
public final class CombinedSchemaGenerator implements SchemaGenerator<CombinedSchema> {

    private static final String ALL_OF = "allOf";
    private static final String ANY_OF = "anyOf";
    private static final String ONE_OF = "oneOf";

    @Override
    public Object generate(@NonNull CombinedSchema schema, @NonNull CrumbPath crumbPath) {

        String criterion = schema.getCriterion().toString();
        Collection<Schema> subSchemas = schema.getSubschemas();

        if (ANY_OF.equals(criterion) || ONE_OF.equals(criterion)) {
            List<Schema> schemasList = new ArrayList<>(subSchemas);
            Schema subSchema = schemasList.get(RandomUtils.nextInt(schemasList.size()));
//            return generate(subSchema, crumbPath); // TODO
            return null; // TODO
        }
        else if (ALL_OF.equals(criterion)) {
            Object result = null;
            for (Schema s :  subSchemas) {
//                result = generate(s, crumbPath); // tODO
                result = null; // tODO
            }
            return result;
        }
        throw new GeneratorException(format("Cannot generate a combined schema [%s] for %s", criterion, "propertyName")); // TODO
    }

}
