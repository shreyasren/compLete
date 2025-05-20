package com.complete.plugin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SysMLModelServiceTest {
    @Test
    public void testExtractEmpty() {
        SysMLModelService svc = new SysMLModelService();
        String ctx = svc.extractModelContext();
        assertNotNull(ctx);
    }

    @Test
    public void testInvalidEndpoint() {
        // configure wrong URL
        System.setProperty("middleware.url", "http://invalid");
        SysMLModelService svc = new SysMLModelService();
        assertThrows(Exception.class, () ->
            svc.requestCompletion("ctx","req")
        );
    }
}