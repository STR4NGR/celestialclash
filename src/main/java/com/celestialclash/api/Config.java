package com.celestialclash.api;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream inputStream = Config.class.getResourceAsStream("/config.properties")) {
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }


}
