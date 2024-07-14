package com.sinaev.configs;

import com.sinaev.factories.YamlPropertySourceFactory;
import com.sinaev.initializers.LiquibaseInitializer;
import com.sinaev.configs.properties.DatasourceProperties;
import com.sinaev.configs.properties.LiquibaseProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Configuration class that loads properties from the application.properties file.
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.sinaev")
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@RequiredArgsConstructor
@Import(DataSourceConfig.class)
public class AppConfig {
    private final Environment env;

    @Bean
    public LiquibaseProperties liquibaseConfig() {
        LiquibaseProperties config = new LiquibaseProperties();
        config.setChangeLogFile(env.getProperty("liquibase.change-log"));
        config.setDefaultSchemaName(env.getProperty("liquibase.default-schema"));
        config.setEntitySchemaName(env.getProperty("liquibase.entity-schema"));
        config.setDatabaseChangeLogTableName(env.getProperty("liquibase.database-change-log-table"));
        config.setDatabaseChangeLogLockTableName(env.getProperty("liquibase.database-change-log-lock-table"));
        return config;
    }

    @Bean
    public DatasourceProperties dataSourceProperties() {
        DatasourceProperties props = new DatasourceProperties();
        props.setUrl(env.getProperty("spring.datasource.url"));
        props.setUsername(env.getProperty("spring.datasource.username"));
        props.setPassword(env.getProperty("spring.datasource.password"));
        return props;
    }

    @Bean
    public LiquibaseInitializer liquibaseInitializer(LiquibaseProperties liquibaseConfig, DatasourceProperties dataSourceConfig) {
        return new LiquibaseInitializer(liquibaseConfig, dataSourceConfig);
    }
}