package com.sinaev.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class that loads Liquibase properties from the liquibase.properties file.
 */
public class LiquibaseConfig {
    private Properties properties = new Properties();

    /**
     * Constructs a LiquibaseConfig object and loads the properties from the liquibase.properties file.
     */
    public LiquibaseConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("liquibase.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find liquibase.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the Liquibase change log file path from the properties file.
     *
     * @return the change log file path
     */
    public String getChangeLogFile() {
        return properties.getProperty("changeLogFile");
    }

    /**
     * Retrieves the database URL from the properties file.
     *
     * @return the database URL
     */
    public String getDbUrl() {
        return properties.getProperty("url");
    }

    /**
     * Retrieves the database username from the properties file.
     *
     * @return the database username
     */
    public String getDbUser() {
        return properties.getProperty("username");
    }

    /**
     * Retrieves the database password from the properties file.
     *
     * @return the database password
     */
    public String getDbPassword() {
        return properties.getProperty("password");
    }

    /**
     * Retrieves the default schema name from the properties file.
     *
     * @return the default schema name
     */
    public String getDefaultSchemaName() {
        return properties.getProperty("defaultSchemaName");
    }

    /**
     * Retrieves the entity schema name from the properties file.
     *
     * @return the entity schema name
     */
    public String getEntitySchemaName() {
        return properties.getProperty("entitySchemaName");
    }

    /**
     * Retrieves the name of the Liquibase change log table from the properties file.
     *
     * @return the change log table name
     */
    public String getDatabaseChangeLogTableName() {
        return properties.getProperty("databaseChangeLogTableName");
    }

    /**
     * Retrieves the name of the Liquibase change log lock table from the properties file.
     *
     * @return the change log lock table name
     */
    public String getDatabaseChangeLogLockTableName() {
        return properties.getProperty("databaseChangeLogLockTableName");
    }
}