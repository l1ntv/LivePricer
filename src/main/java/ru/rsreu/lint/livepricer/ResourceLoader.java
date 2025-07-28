package ru.rsreu.lint.livepricer;

import java.io.InputStream;
import java.util.Properties;

public class ResourceLoader {

    private static final String filename = "config.properties";

    public static String getProperty(String key) {
        Properties prop = new Properties();
        try (InputStream input = ResourceLoader.class.getClassLoader().getResourceAsStream(filename)) {
            if (input == null) {
                System.err.println("Файл не найден: " + filename);
                return null;
            }
            prop.load(input);
            return prop.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
