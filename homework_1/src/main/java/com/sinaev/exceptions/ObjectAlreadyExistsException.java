package com.sinaev.exceptions;

/**
 * Exception thrown when an object already exists.
 * <p>
 * This runtime exception is thrown to indicate that an attempt to create or add an object
 * has failed because the object already exists.
 * </p>
 */
public class ObjectAlreadyExistsException extends RuntimeException{

    /**
     * Constructs a new ObjectAlreadyExistsException with the specified detail message.
     *
     * @param message the detail message
     */
    public ObjectAlreadyExistsException(String message) {
        super(message);
    }
}
