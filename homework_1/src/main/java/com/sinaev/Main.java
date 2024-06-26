package com.sinaev;

import com.sinaev.handlers.ProgramsHandler;

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
        ProgramsHandler handler = new ProgramsHandler();

        Map<String, Runnable> commands = handler.commands();
        handler.uploadInitData();
        handler.startProgram(commands);
    }
}