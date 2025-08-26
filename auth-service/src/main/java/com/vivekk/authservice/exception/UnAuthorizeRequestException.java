package com.vivekk.authservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UnAuthorizeRequestException extends Exception {

    private static final long serialversionuid = 1L;

    private final String code;
    private final String message;

    public UnAuthorizeRequestException() {
        this.code = "UNAUTHORIZED";
        this.message = "Your Request is unauthorized.";
    }

}
