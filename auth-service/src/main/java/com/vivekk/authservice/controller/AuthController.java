package com.vivekk.authservice.controller;

import com.vivekk.authservice.dto.*;
import com.vivekk.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth")
@Validated
@AllArgsConstructor
@Tag(name = "Authentication", description = "APIs related to user authentication")
public class AuthController {


    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/validate/email")
    @Operation(summary = "Validated email", description = "Hashed email value to be passed to check if the email is registered")
    public ResponseEntity<String> validateEmail(@RequestBody @Valid EmailRequest emailRequest){
        String decryptedEmail = authService.validateEmail(emailRequest.getEmailHash());
        return new ResponseEntity<>(decryptedEmail, HttpStatus.OK);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Returns JWT if credentials are valid")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody @Valid AuthRequest authRequest, @RequestHeader(required = false, value = "X-Auth")  String accessToken){
        AuthResponse authResponse = authService.loginUser(authRequest, accessToken);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest){
        TokenResponse tokenResponse = authService.registerUser(registerRequest);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody @Valid TokenRefreshRequest tokenRefreshRequest){
        TokenResponse tokenResponse = authService.validateAndRotateToken(tokenRefreshRequest.getRefreshToken());
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }

    @GetMapping("/otp/{otpLength}")
    public ResponseEntity<String> createOTP(@PathVariable Integer otpLength){
        String otp = authService.createOTP(otpLength);
        return new ResponseEntity<>(otp, HttpStatus.OK);
    }

    @PostMapping("/verify/otp")
    public ResponseEntity<Boolean> verifyOTP(@RequestBody String otp){
        Boolean isVerified = authService.validateOTP(otp);
        return new ResponseEntity<>(isVerified, HttpStatus.OK);
    }
}
