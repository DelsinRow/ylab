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
 *  This class configures the Spring application. It loads properties from a YAML file,
 *  scans for Spring components, and sets up necessary beans like database and Liquibase properties.
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.sinaev")
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@RequiredArgsConstructor
@Import(DataSourceConfig.class)
public class AppConfig {
    private final Environment env;

    /**
     * Configures Liquibase properties.
     *
     * @return configured LiquibaseProperties object
     */
    @Bean
    public LiquibaseProperties liquibaseConfig() {
        LiquibaseProperties config = new LiquibaseProperties();
        config.setChangeLogFile(env.getProperty("liquibase.change-log"));
        config.setDefaultSchemaName(env.getProperty("liquibase.default-schema"));
        config.setEntitySchemaName(env.getProperty("liquibase.entity-schema"));
        return config;
    }

    /**
     * Configures data source properties.
     *
     * @return configured DatasourceProperties object
     */
    @Bean
    public DatasourceProperties dataSourceProperties() {
        DatasourceProperties props = new DatasourceProperties();
        props.setUrl(env.getProperty("spring.datasource.url"));
        props.setUsername(env.getProperty("spring.datasource.username"));
        props.setPassword(env.getProperty("spring.datasource.password"));
        return props;
    }

    /**
     * Initializes Liquibase with the configured properties.
     *
     * @param liquibaseProperties Liquibase properties
     * @param datasourceProperties Data source properties
     * @return configured LiquibaseInitializer object
     */
    @Bean
    public LiquibaseInitializer liquibaseInitializer(LiquibaseProperties liquibaseProperties, DatasourceProperties datasourceProperties) {
        return new LiquibaseInitializer(liquibaseProperties, datasourceProperties);
    }
}