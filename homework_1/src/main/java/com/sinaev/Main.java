package com.sinaev;

import com.sinaev.config.AppConfig;
import com.sinaev.config.LiquibaseConfig;
import com.sinaev.handlers.ProgramsHandler;
import com.sinaev.handlers.SQLQueryHandler;

import java.util.Map;

/**
 * The entry point of the application.
 * Initializes the ProgramsHandler, sets up commands, uploads initial data, and starts the program.
 */
public class Main {

    /**
     * The main method of the application.
     * Initializes the ProgramsHandler, sets up commands, uploads initial data, and starts the program.
     *
     * @param args the command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize application configuration
        AppConfig appConfig = new AppConfig();
        LiquibaseConfig lbConfig = new LiquibaseConfig();

        // Create a LiquibaseRunner to handle database migrations
        MyLiquibaseRunner liquibaseRunner = new MyLiquibaseRunner(
                lbConfig.getChangeLogFile(),
                lbConfig.getDbUrl(),
                lbConfig.getDbUser(),
                lbConfig.getDbPassword(),
                lbConfig.getDefaultSchemaName(),
                lbConfig.getEntitySchemaName(),
                lbConfig.getDatabaseChangeLogTableName(),
                lbConfig.getDatabaseChangeLogLockTableName());

        // Create a ProgramsHandler to manage user commands
        ProgramsHandler handler = new ProgramsHandler(appConfig.getDbUrl(), appConfig.getDbUsername(), appConfig.getDbPassword());

        // Run Liquibase migrations
        liquibaseRunner.runLiquibase();

        // Get the available commands and start the program
        Map<String, Runnable> commands = handler.commands();
        handler.startProgram(commands);
    }
}