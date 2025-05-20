package com.complete.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigLoader {
    private static final Properties PROPS = new Properties();
    static {
        try (InputStream in = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException ignored) {
        }
    }

    private ConfigLoader() {}

    public static String load(String key) {
        return System.getProperty(key, PROPS.getProperty(key));
    }
}

