package com.vivekk.authservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class TokenResponse implements Serializable {

    private String accessToken;
    private String refreshToken;
    private String email;
    private Date refreshTokenExpiresAt;
    private String firstName;
}
