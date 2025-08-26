package com.vivekk.authservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class InvalidSQLProcessException extends RuntimeException{

    private static final long SERIAL_VERSION_UID = 1L;

    private String code;
    private String message;

    public InvalidSQLProcessException() {
        this.code = "INVALID_PROCESS";
        this.message = "Credential creation process is having failures.";
    }

    public InvalidSQLProcessException(String message) {
        this.code = "INVALID_PROCESS";
        this.message = message;
    }

    public InvalidSQLProcessException(String message, Throwable cause) {
        super(message,cause);
    }
}
