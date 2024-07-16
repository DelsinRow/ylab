package com.sinaev.configs;

import com.sinaev.configs.properties.DatasourceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
/**
 * Configuration class for setting up the data source.
 * <p>
 * This class configures the data source bean using properties defined in {@link DatasourceProperties}.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {
    private final DatasourceProperties datasourceProperties;

    /**
     * Configures and returns a data source bean.
     *
     * @return configured DataSource object
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(datasourceProperties.getUrl());
        dataSource.setUsername(datasourceProperties.getUsername());
        dataSource.setPassword(datasourceProperties.getPassword());
        return dataSource;
    }
}
