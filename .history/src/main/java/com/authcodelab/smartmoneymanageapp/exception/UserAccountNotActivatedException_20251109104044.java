package com.authcodelab.smartmoneymanageapp.exception;

/**
 * Exception thrown when a user account is not activated
 */
public class UserAccountNotActivatedException extends RuntimeException {
    
    public UserAccountNotActivatedException(String message) {
        super(message);
    }
    
    public UserAccountNotActivatedException(String message, Throwable cause) {
        super(message, cause);
    }
}