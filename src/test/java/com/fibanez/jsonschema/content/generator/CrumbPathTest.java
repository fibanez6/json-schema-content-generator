package com.fibanez.jsonschema.content.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.fibanez.jsonschema.content.testUtil.TestUtils.FIXTURE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CrumbPathTest {

    @Test
    void shouldThrowException__getNext_nulls() {
        assertThrows(NullPointerException.class, () -> CrumbPath.getNext(null, CrumbPath.ROOT));
        assertThrows(NullPointerException.class, () -> CrumbPath.getNext(FIXTURE.create(String.class), null));
    }

    @Test
    void shouldThrowException_getArrayNext_nulls() {
        assertThrows(NullPointerException.class, () -> CrumbPath.getArrayNext(0, null));
    }

    @Test
    void shouldReturnCrumbPath_root() {
        CrumbPath root = CrumbPath.ROOT;
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
    void shouldReturnNextCrumbPath_fromObjectPaths(String expectedPath) {
        String[] parts = expectedPath.split("\\.");

        CrumbPath previous = CrumbPath.ROOT;
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            CrumbPath current = CrumbPath.getNext(part, previous);

            assertEquals(part, current.getPropertyName());
            assertEquals(previous, current.getPrevious());
            previous = current;
        }
        assertEquals(expectedPath, previous.getPath());
        assertEquals(parts[parts.length -1], previous.getPropertyName());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "$.object[0]",
            "$.object[0].object[1]"
    })
    void shouldReturnNextCrumbPath_fromObjectArrayPaths(String expectedPath) {
        String[] parts = expectedPath.split("\\.");

        CrumbPath previous = CrumbPath.ROOT;
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];

            int index = part.charAt(part.indexOf("[") + 1) - '0';
            String propertyName = part.substring(0, part.indexOf("["));

            // For arrays, the previous node comes from an object '$.object'
            CrumbPath current = CrumbPath.getNext(propertyName, previous);
            current = CrumbPath.getArrayNext(index, current);

            assertEquals(part, current.getPropertyName());
            assertEquals(previous, current.getPrevious());
            previous = current;
        }
        assertEquals(expectedPath, previous.getPath());
        assertEquals(parts[parts.length -1], previous.getPropertyName());
    }

    @Test
    void shouldReturnNextCrumbPath_fromInitArrayPaths() {
        String expectedPath = "$[0]";
        CrumbPath crumbPath = CrumbPath.getArrayNext(0, CrumbPath.ROOT);

        assertEquals(expectedPath, crumbPath.getPath());
        assertEquals(expectedPath, crumbPath.getPropertyName());
    }

    @Test
    void shouldReturnNextCrumbPath_fromArrayOfArraysPaths() {
        String expectedPath = "$[0][1]";

        CrumbPath crumbPath = CrumbPath.getArrayNext(0, CrumbPath.ROOT);
        crumbPath = CrumbPath.getArrayNext(1, crumbPath);

        assertEquals(expectedPath, crumbPath.getPath());
        assertEquals(expectedPath, crumbPath.getPropertyName());
    }

}