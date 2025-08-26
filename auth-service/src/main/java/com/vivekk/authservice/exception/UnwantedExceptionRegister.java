package com.vivekk.authservice.exception;

import lombok.Getter;

@Getter
public class UnwantedExceptionRegister extends RuntimeException {
    private static final long serialVersionUID = 1L;  // Fixed case to follow convention

    private final String errorCode;  // More conventional field name for error codes

    // Default constructor with default values
    public UnwantedExceptionRegister() {
        this("INVALID_PROCESS", "Unwanted exception occurred while registering new user");
    }

    // Constructor with custom message but default code
    public UnwantedExceptionRegister(String message) {
        this("INVALID_PROCESS", message);
    }

    // Main constructor with both code and message
    public UnwantedExceptionRegister(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    // Constructor with message and cause (using default code)
    public UnwantedExceptionRegister(String message, Throwable cause) {
        this("INVALID_PROCESS", message, cause);
    }

    // Full constructor with code, message and cause
    public UnwantedExceptionRegister(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}