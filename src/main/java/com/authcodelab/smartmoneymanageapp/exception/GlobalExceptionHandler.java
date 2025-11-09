package com.authcodelab.smartmoneymanageapp.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Money Management App
 * Handles various exceptions and returns appropriate HTTP responses
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("status", "error");
        response.put("message", "Validation failed");
        response.put("errors", errors);

        log.error("Validation errors: {}", errors);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle JWT related exceptions
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwtException(ExpiredJwtException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "JWT token has expired");
        response.put("error_code", "TOKEN_EXPIRED");

        log.error("JWT token expired: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<Map<String, Object>> handleMalformedJwtException(MalformedJwtException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Invalid JWT token format");
        response.put("error_code", "INVALID_TOKEN");

        log.error("Malformed JWT token: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, Object>> handleSignatureException(SignatureException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "JWT signature validation failed");
        response.put("error_code", "INVALID_SIGNATURE");

        log.error("JWT signature validation failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handle authentication errors
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Invalid email or password");
        response.put("error_code", "INVALID_CREDENTIALS");

        log.error("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "User not found");
        response.put("error_code", "USER_NOT_FOUND");

        log.error("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle custom application exceptions
     */
    @ExceptionHandler(UserAccountNotActivatedException.class)
    public ResponseEntity<Map<String, Object>> handleUserAccountNotActivatedException(
            UserAccountNotActivatedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());
        response.put("error_code", "ACCOUNT_NOT_ACTIVATED");

        log.error("Account not activated: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEmailException(DuplicateEmailException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());
        response.put("error_code", "DUPLICATE_EMAIL");

        log.error("Duplicate email: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidActivationTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidActivationTokenException(
            InvalidActivationTokenException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());
        response.put("error_code", "INVALID_ACTIVATION_TOKEN");

        log.error("Invalid activation token: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle generic runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "An error occurred while processing your request");
        response.put("error_code", "INTERNAL_ERROR");

        log.error("Runtime exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "An unexpected error occurred");
        response.put("error_code", "UNEXPECTED_ERROR");

        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}