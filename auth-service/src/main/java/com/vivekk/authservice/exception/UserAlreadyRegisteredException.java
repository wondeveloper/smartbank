package com.vivekk.authservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UserAlreadyRegisteredException extends RuntimeException{

    private static final long SERIAL_VERSION_UID = 1L;

    private String code;
    private String message;

    public UserAlreadyRegisteredException() {
        this.code = "INVALID_PROCESS";
        this.message = "Credential creation process is having failures.";
    }

    public UserAlreadyRegisteredException(String message) {
        this.code = "INVALID_PROCESS";
        this.message = message;
    }

    public UserAlreadyRegisteredException(String message, Throwable cause) {
        super(message,cause);
    }
}
