package com.sinaev.handlers;

import com.sinaev.config.AppConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handler for executing SQL queries.
 */
public class SQLQueryHandler {

    /**
     * Sets the search path for the database connection to the schema specified in the application configuration.
     *
     * @param connection the database connection
     * @throws RuntimeException if an SQL exception occurs
     */
    public void addSearchPathPrivate(Connection connection) {
        String schemaName = new AppConfig().getSchema();
        String setSQL = "SET search_path TO " + schemaName;
        try {
            Statement statement = connection.createStatement();
            statement.execute(setSQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}