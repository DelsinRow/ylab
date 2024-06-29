package com.sinaev;

import liquibase.Scope;
import liquibase.command.CommandScope;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles the execution of Liquibase migrations.
 */
@RequiredArgsConstructor
public class MyLiquibaseRunner {
    private final String changelogFile;
    private final String urlDb;
    private final String usernameDb;
    private final String passwordDb;
    private final String defaultSchemaName;
    private final String entitySchemaName;
    private final String databaseChangeLogTableName;
    private final String databaseChangeLogLockTableName;

    /**
     * Runs the Liquibase update command.
     */
    public void runLiquibase() {
        System.out.println("Running Liquibase...");
        createSchema(defaultSchemaName);
        createSchema(entitySchemaName);

        try {
            Scope.child(Scope.Attr.resourceAccessor, new ClassLoaderResourceAccessor(), () -> {
                CommandScope update = new CommandScope("update");

                update.addArgumentValue("changelogFile", changelogFile);
                update.addArgumentValue("url", urlDb);
                update.addArgumentValue("username", usernameDb);
                update.addArgumentValue("password", passwordDb);
                update.addArgumentValue("defaultSchemaName", defaultSchemaName);
                update.addArgumentValue("entitySchemaName", entitySchemaName);
                update.addArgumentValue("databaseChangeLogTableName", databaseChangeLogTableName);
                update.addArgumentValue("databaseChangeLogLockTableName", databaseChangeLogLockTableName);

                update.execute();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Running Liquibase...DONE");
    }

    /**
     * Creates the specified schema if it does not exist.
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
