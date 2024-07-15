package com.sinaev.handlers;

import com.sinaev.configs.AppConfig;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Handler for executing SQL queries.
 */
public class SQLQueryHandler {

    public void addSearchPathPrivate(Connection connection) {
        String setSQL = "SET search_path TO entity_schema";
        try {
            Statement statement = connection.createStatement();
            statement.execute(setSQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}