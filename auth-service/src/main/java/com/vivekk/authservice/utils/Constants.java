package com.vivekk.authservice.utils;

public interface Constants {
    
    String FORWARD_ID = "X-FORWARD-ID";
    String X_CORRELATION_ID = "X-CORRELATION-ID";
    String INVALID_TOKEN = "Refresh Token is invalid";
    String DEFAULT_ALGORITHM = "HmacSha1";
    String OTP_SECRET = "32986298";

    static interface Login{
        String ACCESS_TOKEN = "access_token";
        String ANONYMOUS_ACCESS_TOKEN = "anonymous_access_token";
        String DELETE_ACCOUNT = "delete_account";
        String REFRESH_TOKEN = "refresh_token";
        String LOGIN_KEY="login_key";
        String ACCESSTOKEN_ISSUER="accessToken.issuer";
        String REFRESHTOKEN_SECRETKEY="refreshToken.secretKey";
        String ACCESSTOKEN_TTLMILLIES="accessToken.ttlMillis";
        String ACCESSTOKEN_SECRETKEY="accessToken.secretKey";
        String REFRESHTOKEN_TTLMILLIES="refreshToken.ttlMillis";
        String WEB ="web";
        String ANDROID="android";
        String IOS="ios";
        String VALIDATE_SECRET_KEY = "validate.secret.key";
    }
}
