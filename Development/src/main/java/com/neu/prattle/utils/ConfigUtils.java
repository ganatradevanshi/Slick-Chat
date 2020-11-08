package com.neu.prattle.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * This class provides utility functions to grab configuration properties at run-time.
 */
public class ConfigUtils {

    private static final String PROPERTY_FILE_PATH = "config.properties";
    private static ConfigUtils utils;
    private Properties prop = new Properties();
    private InputStream isProps;

    private ConfigUtils() {
        isProps = this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE_PATH);
        try {
            prop.load(this.isProps);
        } catch (Exception e) {
            throw new IllegalStateException("Could not load the config file");
        }
    }

    public static ConfigUtils getInstance() {
        if (utils == null) {
            utils = new ConfigUtils();
        }
        return utils;
    }

    public ConfigUtils(String filePath) {
        isProps = this.getClass().getClassLoader().getResourceAsStream(filePath);
        try {
            prop.load(this.isProps);
        } catch (Exception e) {
            throw new IllegalStateException("Could not load the config file");
        }
    }

    /**
     * Get value of the key provided.
     *
     * @param key - key to be used to find the config value.
     * @return - value of the key.
     */
    public String getPropertyValue(String key) {
        return prop.getProperty(key);
    }
}
