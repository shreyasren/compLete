package com.complete.plugin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests for {@link SysMLModelService}.
 */
public class SysMLModelServiceTest {

    @Test
    public void handlesResponsesMissingArrays() {
        SysMLModelService svc = new SysMLModelService();
        String json = "{}";
        assertDoesNotThrow(() -> svc.applySuggestions(json));
    }
}
