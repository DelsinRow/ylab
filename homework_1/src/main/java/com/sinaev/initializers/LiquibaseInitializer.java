package com.sinaev.initializers;

import com.sinaev.MyLiquibaseRunner;
import com.sinaev.configs.properties.DatasourceProperties;
import com.sinaev.configs.properties.LiquibaseProperties;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LiquibaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(LiquibaseInitializer.class);

    private final LiquibaseProperties lbProps;
    private final DatasourceProperties dbProps;

    @PostConstruct
    public void init() {
        logger.info("Initializing Liquibase...");

        MyLiquibaseRunner liquibaseRunner = MyLiquibaseRunner.builder()
                .changelogFile(lbProps.getChangeLogFile())
                .urlDb(dbProps.getUrl())
                .usernameDb(dbProps.getUsername())
                .passwordDb(dbProps.getPassword())
                .defaultSchemaName(lbProps.getDefaultSchemaName())
                .entitySchemaName(lbProps.getEntitySchemaName())
                .build();

        logger.info("Running Liquibase...");
        liquibaseRunner.runLiquibase();
        logger.info("Liquibase run completed.");
    }
}