package com.fibanez.jsonschema.content.generator.schemaMerger;

import com.fibanez.jsonschema.content.generator.exception.GeneratorException;
import org.everit.json.schema.NotSchema;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.Schema;

import java.util.List;
import java.util.Map;

final class ObjectSchemaMerger implements SchemaMerger {

    private final ObjectSchema.Builder schemaBuilder;

    ObjectSchemaMerger() {
        this.schemaBuilder = new ObjectSchema.Builder();
    }

    @Override
    public Schema getSchema() {
        return schemaBuilder.build();
    }

    @Override
    public ObjectSchemaMerger combine(Schema schema) {
        if (schema instanceof ObjectSchema) {
            doCombine((ObjectSchema) schema);
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema.getClass());
        }
        return this;
    }

    @Override
    public ObjectSchemaMerger not(Schema schema) {
        if (schema instanceof ObjectSchema) {
            doNot((ObjectSchema) schema);
        } else {
            throw new GeneratorException("Unsupported merge schema '%s'", schema.getClass());
        }
        return this;
    }

    private void doCombine(ObjectSchema schema) {
        combinePropertySchemas(schema);

        if (schema.getMinProperties() != null) {
            schemaBuilder.minProperties(schema.getMinProperties());
        }
        if (schema.getMaxProperties() != null) {
            schemaBuilder.maxProperties(schema.getMaxProperties());
        }
        if (schema.getDefaultValue() != null) {
            schemaBuilder.defaultValue(schema.getDefaultValue());
        }
        if (schema.getPropertyNameSchema() != null) {
            schemaBuilder.propertyNameSchema(schema.getPropertyNameSchema());
        }
        if (schema.getSchemaOfAdditionalProperties() != null) {
            schemaBuilder.schemaOfAdditionalProperties(schema.getSchemaOfAdditionalProperties());
        }
        Map<String, Object> unprocessedProperties = schema.getUnprocessedProperties();
        if (unprocessedProperties != null && !unprocessedProperties.isEmpty()) {
            schemaBuilder.unprocessedProperties(unprocessedProperties);
        }
        if (schema.getPropertyDependencies() != null) {
            schema.getPropertyDependencies()
                    .forEach((prop, dependencies) -> dependencies
                            .forEach(dep -> schemaBuilder.propertyDependency(prop, dep)));
        }

        List<String> requiredProperties = schema.getRequiredProperties();
        if (requiredProperties != null) {
            requiredProperties.forEach(schemaBuilder::addRequiredProperty);
        }

        if (schema.getSchemaDependencies() != null) {
            schema.getSchemaDependencies().forEach(schemaBuilder::schemaDependency);
        }

        schemaBuilder.additionalProperties(schema.permitsAdditionalProperties());
    }

    private void doNot(ObjectSchema schema) {
        if (schema.getMinProperties() != null) {
            schemaBuilder.minProperties(null);
            schemaBuilder.maxProperties(schema.getMinProperties());
        }

        if (schema.getMaxProperties() != null) {
            schemaBuilder.minProperties(schema.getMaxProperties());
            schemaBuilder.maxProperties(null);
        }

        negatePropertySchemas(schema);
    }

    private void combinePropertySchemas(ObjectSchema schema) {
        Map<String, Schema> propertySchemas = schema.getPropertySchemas();
        Map<String, Schema> propSchemaFromBuilder = schemaBuilder.build().getPropertySchemas();

        propertySchemas.forEach((prop, propSchema) -> {
            if (propSchemaFromBuilder.containsKey(prop)) {
                Schema schemaFromBuild = propSchemaFromBuilder.get(prop);
                Schema combined = combineSchemas(schemaFromBuild, propSchema);
                schemaBuilder.addPropertySchema(prop, combined);
            } else {
                schemaBuilder.addPropertySchema(prop, propSchema);
            }
        });
    }

    private void negatePropertySchemas(ObjectSchema schema) {
        Map<String, Schema> propertySchemas = schema.getPropertySchemas();
        Map<String, Schema> propSchemaFromBuilder = schemaBuilder.build().getPropertySchemas();

        propertySchemas.forEach((prop, propSchema) -> {
            if (propSchemaFromBuilder.containsKey(prop)) {
                Schema schemaToNegate = propSchemaFromBuilder.get(prop);
                Schema negated = negatedSchemas(schemaToNegate, propSchema);
                schemaBuilder.addPropertySchema(prop, negated);
            }
        });
    }

    private Schema combineSchemas(Schema schema, Schema toCombine) {
        List<Schema> schemas = List.of(schema, toCombine);
        return SchemaCombinator.combine(schemas);
    }

    private Schema negatedSchemas(Schema schema, Schema toNegate) {
        List<Schema> schemas = List.of(schema, NotSchema.builder().mustNotMatch(toNegate).build());
        return SchemaCombinator.combine(schemas);
    }

}
