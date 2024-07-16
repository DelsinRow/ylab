package com.sinaev.exceptions;

/**
 * Exception thrown when a username is already taken.
 * <p>
 * This runtime exception is thrown to indicate that an attempt to register or change
 * a username has failed because the username is already taken.
 * </p>
 */
public class UsernameAlreadyTakenException extends RuntimeException{

    /**
     * Constructs a new UsernameAlreadyTakenException with the specified detail message.
     *
     * @param message the detail message
     */
    public UsernameAlreadyTakenException(String message) {
        super(message);
    }
}
