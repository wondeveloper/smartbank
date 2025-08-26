package com.vivekk.authservice.service;

import com.vivekk.authservice.dto.AuthRequest;
import com.vivekk.authservice.dto.Credentials;
import com.vivekk.authservice.dto.TokenResponse;
import com.vivekk.authservice.entity.RefreshToken;
import com.vivekk.authservice.exception.InvalidTokenException;
import com.vivekk.authservice.exception.UnAuthorizeRequestException;
import io.jsonwebtoken.Claims;

public interface LoginService {

    Claims parseAndVerifyToken(String token) throws UnAuthorizeRequestException;

    Credentials getAccessTokens(AuthRequest authRequest) throws InvalidTokenException;

    TokenResponse rotateToken(String refreshToken);

    RefreshToken updateAndCache(String token, RefreshToken refreshToken);

    String validateEmail(String emailHash);

    String createTotp(Integer otpLength, Long timeWindow);
}
