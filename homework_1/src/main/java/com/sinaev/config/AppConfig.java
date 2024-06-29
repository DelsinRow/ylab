package com.sinaev.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class that loads properties from the application.properties file.
 */
public class AppConfig {

    private Properties properties = new Properties();

    /**
     * Constructs an AppConfig object and loads the properties from the application.properties file.
     */
    public AppConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the database URL from the properties file.
     *
     * @return the database URL
     */
    public String getDbUrl() {
        return properties.getProperty("database.url");
    }

    /**
     * Retrieves the database username from the properties file.
     *
     * @return the database username
     */
    public String getDbUsername() {
        return properties.getProperty("database.username");
    }

    /**
     * Retrieves the database password from the properties file.
     *
     * @return the database password
     */
    public String getDbPassword() {
        return properties.getProperty("database.password");
    }

    /**
     * Retrieves the database schema from the properties file.
     *
     * @return the database schema
     */
    public String getSchema() {
        return properties.getProperty("database.schema");
    }
}