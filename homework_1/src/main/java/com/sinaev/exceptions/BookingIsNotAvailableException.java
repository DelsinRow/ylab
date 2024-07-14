package com.sinaev.exceptions;

public class BookingIsNotAvailableException extends RuntimeException{
    public BookingIsNotAvailableException(String message) {
        super(message);
    }
}
