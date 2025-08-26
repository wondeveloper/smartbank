package com.vivekk.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TokenRefreshRequest implements Serializable {

    @NotBlank
    private String refreshToken;
}
