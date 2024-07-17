package com.sinaev.configs.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for Liquibase.
 * <p>
 * This class holds the properties required to configure Liquibase, including
 * the change log file, default schema name, and entity schema name.
 * </p>
 */
@Getter
@Setter
public class LiquibaseProperties {
    private String changeLogFile;
    private String defaultSchemaName;
    private String entitySchemaName;
}