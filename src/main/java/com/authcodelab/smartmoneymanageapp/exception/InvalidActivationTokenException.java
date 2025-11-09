package com.authcodelab.smartmoneymanageapp.exception;

/**
 * Exception thrown when an invalid activation token is used
 */
public class InvalidActivationTokenException extends RuntimeException {

    public InvalidActivationTokenException(String message) {
        super(message);
    }

    public InvalidActivationTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}