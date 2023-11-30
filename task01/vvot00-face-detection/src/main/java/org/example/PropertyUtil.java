package org.example;

import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {
    static String getProperty(String propertyName) {
        var propertiesStream = Main.class
                .getClassLoader()
                .getResourceAsStream("application.properties");
        var properties = new Properties();
        try {
            properties.load(propertiesStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty(propertyName);
    }
}
