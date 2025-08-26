package com.vivekk.authservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class TokenNotFound extends RuntimeException{

    private String code;
    private String message;

    public TokenNotFound(String message) {
        this.code = "INVALID";
        this.message = message;
    }

    public TokenNotFound(String message, Throwable ex){
        super(message, ex);
    }
}
