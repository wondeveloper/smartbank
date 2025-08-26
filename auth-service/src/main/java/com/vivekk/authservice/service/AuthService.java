package com.vivekk.authservice.service;


import com.vivekk.authservice.dto.AuthRequest;
import com.vivekk.authservice.dto.AuthResponse;
import com.vivekk.authservice.dto.RegisterRequest;
import com.vivekk.authservice.dto.TokenResponse;

public interface AuthService {

    AuthResponse loginUser(AuthRequest authRequest, String token);

    TokenResponse validateAndRotateToken(String token);

    String  validateEmail(String emailHash);

    String createOTP(Integer otpLength);

    Boolean validateOTP(String otp);

    TokenResponse registerUser(RegisterRequest registerRequest);
}
