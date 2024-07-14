package com.sinaev;

import com.sinaev.configs.DatasourceProperties;
import com.sinaev.configs.LiquibaseProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class LiquibaseInitializer{
    private final LiquibaseProperties lbPropsl;
    private final DatasourceProperties dbProps;
    @PostConstruct
    public void init() {

        MyLiquibaseRunner liquibaseRunner = MyLiquibaseRunner.builder()
                .changelogFile(lbPropsl.getChangeLogFile())
                .urlDb(dbProps.getUrl())
                .usernameDb(dbProps.getUsername())
                .passwordDb(dbProps.getPassword())
                .defaultSchemaName(lbPropsl.getDefaultSchemaName())
                .entitySchemaName(lbPropsl.getEntitySchemaName())
                .databaseChangeLogTableName(lbPropsl.getDatabaseChangeLogTableName())
                .databaseChangeLogTableName(lbPropsl.getDatabaseChangeLogLockTableName())
                .build();

        liquibaseRunner.runLiquibase();

    }

}
