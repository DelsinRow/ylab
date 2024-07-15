package com.sinaev;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles the execution of Liquibase migrations.
 */
@Builder
public class MyLiquibaseRunner {
    private final String changelogFile;
    private final String urlDb;
    private final String usernameDb;
    private final String passwordDb;
    private final String defaultSchemaName;
    private final String entitySchemaName;
    private final String databaseChangeLogTableName;
    private final String databaseChangeLogLockTableName;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load PostgreSQL driver", e);
        }
    }

    public void runLiquibase() {
        createSchema(entitySchemaName);
        createSchema(defaultSchemaName);

        try (Connection connection = DriverManager.getConnection(urlDb, usernameDb, passwordDb)) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            database.setDefaultSchemaName(defaultSchemaName);
            database.setLiquibaseCatalogName(entitySchemaName);

            Liquibase liquibase = new Liquibase(changelogFile, new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (Exception e) {
            throw new RuntimeException("Error running Liquibase", e);
        }
    }

    private void createSchema(String schemaName) {
        String createSchemaSQL = "CREATE SCHEMA IF NOT EXISTS " + schemaName;

        try (Connection connection = DriverManager.getConnection(urlDb, usernameDb, passwordDb);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(createSchemaSQL);
            System.out.println("Schema " + schemaName + " created or already exists.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create schema: " + schemaName, e);
        }
    }
}
