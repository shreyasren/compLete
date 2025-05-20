package com.complete.plugin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {
    @Test
    public void loadsDefaultProperty() {
        System.setProperty("middleware.url", "http://example");
        assertEquals("http://example", ConfigLoader.load("middleware.url"));
    }
}

