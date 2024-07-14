package com.sinaev.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class LiquibaseProperties {
    private String changeLogFile;
    private String defaultSchemaName;
    private String entitySchemaName;
    private String databaseChangeLogTableName;
    private String databaseChangeLogLockTableName;
}