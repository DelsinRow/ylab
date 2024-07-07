package com.sinaev;

import com.sinaev.aspects.AuditAspect;
import com.sinaev.configs.LiquibaseConfig;
import com.sinaev.repositories.AuditLogRepository;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class LiquibaseInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LiquibaseConfig lbConfig = new LiquibaseConfig();

        MyLiquibaseRunner liquibaseRunner = new MyLiquibaseRunner(
                lbConfig.getChangeLogFile(),
                lbConfig.getDbUrl(),
                lbConfig.getDbUser(),
                lbConfig.getDbPassword(),
                lbConfig.getDefaultSchemaName(),
                lbConfig.getEntitySchemaName(),
                lbConfig.getDatabaseChangeLogTableName(),
                lbConfig.getDatabaseChangeLogLockTableName()
        );

        liquibaseRunner.runLiquibase();

        AuditLogRepository auditLogRepository = new AuditLogRepository(lbConfig.getDbUrl(), lbConfig.getDbUser(), lbConfig.getDbPassword());
        AuditAspect.init(auditLogRepository);
    }

}
