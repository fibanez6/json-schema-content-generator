package com.fibanez.jsonschema.content.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonNodeTest {

    @Test
    void shouldThrowException__getNext_nulls() {
        assertThrows(NullPointerException.class, () -> JsonNode.ROOT.getNext(null));
    }

    @Test
    void shouldReturnJsonNode_root() {
        JsonNode root = JsonNode.ROOT;
        assertNotNull(root);
        assertEquals("$", root.getPath());
        assertNull(root.getPropertyName());
        assertNull(root.getPrevious());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "$.object_01",
            "$.object_01.object_011",
            "$.object_01.object_011.object_0111"
    })
    void shouldReturnNextJsonNode_fromObjectPaths(String expectedPath) {
        String[] parts = expectedPath.split("\\.");

        JsonNode current = JsonNode.ROOT;
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            JsonNode next = current.getNext(part);

            assertEquals(part, next.getPropertyName());
            assertEquals(current, next.getPrevious());
            current = next;
        }
        assertEquals(expectedPath, current.getPath());
        assertEquals(parts[parts.length - 1], current.getPropertyName());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "$.object[0]",
            "$.object[0].object[1]",
            "$.object[0].object[1].object[2]"
    })
    void shouldReturnNextJsonNode_fromObjectArrayPaths(String expectedPath) {
        String[] parts = expectedPath.split("\\.");

        JsonNode current = JsonNode.ROOT;
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];

            int index = part.charAt(part.indexOf("[") + 1) - '0';
            String propertyName = part.substring(0, part.indexOf("["));

            // For arrays, the previous node comes from an object '$.object'
            JsonNode next = current.getNext(propertyName);
            next = next.getArrayNext(index);

            assertEquals(propertyName, next.getPropertyName());
            assertEquals(current, next.getPrevious());
            current = next;
        }
        assertEquals(expectedPath, current.getPath());
        // Assert last property name from the chain
        String lastPropertyName = parts[parts.length - 1];
        String expectedPropertyName = lastPropertyName.substring(0, lastPropertyName.indexOf("["));
        assertEquals(expectedPropertyName, current.getPropertyName());
    }

    @Test
    void shouldReturnNextJsonNode_fromInitArrayPaths() {
        String expectedPath = "$[0]";
        JsonNode jsonNode = JsonNode.ROOT.getArrayNext(0);

        assertEquals(expectedPath, jsonNode.getPath());
        assertNull(jsonNode.getPropertyName());
    }

    @Test
    void shouldReturnNextJsonNode_fromArrayOfArraysPaths() {
        String expectedPath = "$[0][1]";

        JsonNode jsonNode = JsonNode.ROOT.getArrayNext(0);
        jsonNode = jsonNode.getArrayNext(1);

        assertEquals(expectedPath, jsonNode.getPath());
        assertNull(jsonNode.getPropertyName());
    }

}