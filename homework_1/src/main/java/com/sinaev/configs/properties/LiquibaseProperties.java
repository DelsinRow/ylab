package com.sinaev.configs.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LiquibaseProperties {
    private String changeLogFile;
    private String defaultSchemaName;
    private String entitySchemaName;
    private String databaseChangeLogTableName;
    private String databaseChangeLogLockTableName;
}