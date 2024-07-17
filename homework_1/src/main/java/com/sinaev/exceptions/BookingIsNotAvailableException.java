package com.sinaev.exceptions;

/**
 * Exception thrown when a booking is not available.
 * <p>
 * This runtime exception is thrown to indicate that a booking attempt has failed
 * because the requested booking is not available.
 * </p>
 */
public class BookingIsNotAvailableException extends RuntimeException{

    /**
     * Constructs a new BookingIsNotAvailableException with the specified detail message.
     *
     * @param message the detail message
     */
    public BookingIsNotAvailableException(String message) {
        super(message);
    }
}
