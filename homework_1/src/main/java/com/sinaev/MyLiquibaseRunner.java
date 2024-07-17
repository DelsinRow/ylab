package com.sinaev;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.Builder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles the execution of Liquibase migrations.
 * <p>
 * This class is responsible for setting up the database schemas and running Liquibase
 * to apply database changes defined in the changelog file.
 * </p>
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

    /**
     * Runs Liquibase to apply the database changes.
     * <p>
     * This method sets up the schemas and runs the Liquibase migrations.
     * </p>
     */
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

    /**
     * Creates a schema if it does not already exist.
     *
     * @param schemaName the name of the schema to create
     */
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
