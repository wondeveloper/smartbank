package com.vivekk.authservice.service;

import com.vivekk.authservice.dto.*;
import com.vivekk.authservice.entity.RefreshToken;
import com.vivekk.authservice.entity.UserEntity;
import com.vivekk.authservice.exception.InvalidSQLProcessException;
import com.vivekk.authservice.exception.TokenNotFound;
import com.vivekk.authservice.exception.UnwantedExceptionRegister;
import com.vivekk.authservice.exception.UserAlreadyRegisteredException;
import com.vivekk.authservice.repository.TokenRepository;
import com.vivekk.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
@Data
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private LoginService loginService;

    private TokenRepository tokenRepository;

    private UserRepository userRepository;

    @Override
    @Transactional
    public AuthResponse loginUser(AuthRequest authRequest, String token) {
        Credentials credentials = loginService.getAccessTokens(authRequest);
        RefreshToken refreshToken = RefreshToken.builder().token(credentials.getRefreshToken()).email(authRequest.getEmail())
                .expiresAt(credentials.getRefreshTokenExpiresAt()).createdAt(Date.from(Instant.now())).isRevoked(false).build();
        RefreshToken refreshTokenRes = loginService.updateAndCache(credentials.getRefreshToken(), refreshToken);
        log.info("refreshTokenRes saved in db for email: {}", refreshTokenRes.getEmail());
        UserEntity userDetails = userRepository.findByEmailId(authRequest.getEmail()).orElse(null);
        if (null != userDetails){
            return AuthResponse.builder().credentials(credentials)
                    .userDetails(UserDetails.builder().firstName(userDetails.getFirstName()).email(authRequest.getEmail())
                            .LastName(userDetails.getLastName()).build())
                    .build();
        }else {
            return AuthResponse.builder().credentials(credentials)
                    .userDetails(UserDetails.builder().firstName("Guest").email(authRequest.getEmail()).build())
                    .build();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @CacheEvict(value = "refreshTokens", key = "#token")
    public TokenResponse validateAndRotateToken(String token) {
        log.info("validateAndRotateToken : {}", token);
        TokenResponse tokenResponse = loginService.rotateToken(token);
        RefreshToken refreshToken = RefreshToken.builder().token(tokenResponse.getRefreshToken()).email(tokenResponse.getEmail())
                .expiresAt(tokenResponse.getRefreshTokenExpiresAt()).createdAt(Date.from(Instant.now())).isRevoked(false).build();
        RefreshToken refreshTokenRes = tokenRepository.save(refreshToken);
        log.info("refreshTokenRes saved in db for email: {}", refreshTokenRes.getEmail());
        return tokenResponse;
    }

    @Override
    public String validateEmail(String emailHash) {
        return  loginService.validateEmail(emailHash);
    }

    @Override
    public String createOTP(Integer otpLength) {
        log.info("Request received to create OTP of length {}", otpLength);
        long timeWindow = System.currentTimeMillis() / 60_000;
        return loginService.createTotp(otpLength, timeWindow);
    }

    @Override
    public Boolean validateOTP(String otpEntered) {
        long timeWindow = System.currentTimeMillis() / 60_000;
        String expectedOtp = loginService.createTotp(otpEntered.length(), timeWindow);
        String expectedOtp1 = loginService.createTotp(otpEntered.length(), timeWindow-1);
        String expectedOtp2 = loginService.createTotp(otpEntered.length(), timeWindow+1);
        return otpEntered.equals(expectedOtp) || otpEntered.equals(expectedOtp1) || otpEntered.equals(expectedOtp2);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public TokenResponse registerUser(RegisterRequest registerRequest) {
        UserEntity userDetails = userRepository.findByEmailId(registerRequest.getEmailId()).orElse(null);
        if (null != userDetails){
            throw new UserAlreadyRegisteredException("User is already a registered user");
        }
        try {
            Credentials credentials = loginService.getAccessTokens(registerRequest.getAuthRequest());
            UserEntity userEntity = new UserEntity(registerRequest.getFirstName(), registerRequest.getLastName(), registerRequest.getEmailId(), false, false,
            LocalDateTime.now(),LocalDateTime.now());
            UserEntity userEntityRes = userRepository.save(userEntity);
            log.info("User registered :: {}", userEntityRes);
            return TokenResponse.builder().accessToken(credentials.getAccessToken()).refreshToken(credentials.getRefreshToken())
                    .refreshTokenExpiresAt(credentials.getRefreshTokenExpiresAt()).firstName(registerRequest.getFirstName()).email(credentials.getEmailHash()).build();
        }catch (IllegalArgumentException ex){
            throw new InvalidSQLProcessException("Exception occurred while saving the entity", ex);
        }catch (Exception ex){
            throw new UnwantedExceptionRegister("Exception occurred while saving the entity", ex);
        }

    }
}
