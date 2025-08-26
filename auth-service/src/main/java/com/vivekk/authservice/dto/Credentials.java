package com.vivekk.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Credentials {

    private String accessToken;
    private String refreshToken;
    private String emailHash;
    private Date accessTokenExpiresAt;
    private Date refreshTokenExpiresAt;
}
