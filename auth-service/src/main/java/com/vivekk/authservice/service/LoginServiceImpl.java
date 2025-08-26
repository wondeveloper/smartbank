package com.vivekk.authservice.service;

import com.vivekk.authservice.config.JwtUtils;
import com.vivekk.authservice.dto.AuthRequest;
import com.vivekk.authservice.dto.Credentials;
import com.vivekk.authservice.dto.TokenResponse;
import com.vivekk.authservice.entity.RefreshToken;
import com.vivekk.authservice.exception.InvalidTokenException;
import com.vivekk.authservice.exception.SecurityException;
import com.vivekk.authservice.exception.TokenNotFound;
import com.vivekk.authservice.exception.UnAuthorizeRequestException;
import com.vivekk.authservice.repository.TokenRepository;
import com.vivekk.authservice.utils.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static com.vivekk.authservice.utils.Constants.INVALID_TOKEN;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService{

    private final TokenRepository tokenRepository;

    @Value("${accessToken.secretKey}")
    private String secretKey;

    @Value("${accessToken.issuer}")
    private String issuer;

    @Value("${accessToken.ttlMillis}")
    private String ttlMillis;

    @Value("${refreshToken.ttlMillis}")
    private String refreshTtlMillis;
    @Override
    public Claims parseAndVerifyToken(String token) throws UnAuthorizeRequestException {
        try {
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)).build();
            return jwtParser.parseClaimsJwt(token).getBody();
        } catch (IllegalArgumentException ex){
            throw new UnAuthorizeRequestException("UnAuthorized", "Unable to parse token");
        }
    }

    @Override
    public Credentials getAccessTokens(AuthRequest authRequest) throws InvalidTokenException {
        try {
            String accessToken = JwtUtils.createAccessToken(authRequest,issuer,Long.parseLong(ttlMillis),secretKey, Constants.Login.ACCESS_TOKEN);
            String refreshToken = JwtUtils.createAccessToken(authRequest,issuer,Long.parseLong(refreshTtlMillis),secretKey, Constants.Login.REFRESH_TOKEN);
            String passwordHashToken = JwtUtils.createHashToken(authRequest,issuer,secretKey);
            long accessExpiryMillis = System.currentTimeMillis() + Long.parseLong(ttlMillis);
            long refreshExpiryMillis = System.currentTimeMillis() + Long.parseLong(refreshTtlMillis);
            return Credentials.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .emailHash(passwordHashToken)
                    .accessTokenExpiresAt(Date.from(Instant.ofEpochMilli(accessExpiryMillis)))
                    .refreshTokenExpiresAt(Date.from(Instant.ofEpochMilli(refreshExpiryMillis)))
                    .build();
        }catch (Exception ex){
            log.error("Exception occurred while creating credentials for user: {}", authRequest.getEmail());
            throw new InvalidTokenException("Error generating tokens", ex);
        }
    }

    @Override
    public TokenResponse rotateToken(String refreshToken) {
        if (!JwtUtils.validateToken(refreshToken, secretKey, issuer))
            throw new InvalidTokenException("Refresh Token is expired");
        RefreshToken refreshTokenRes;
        try {
            refreshTokenRes = this.findByToken(refreshToken)
                    .orElseThrow(() -> new TokenNotFound(INVALID_TOKEN));
        } catch (Exception ex) {
            throw new TokenNotFound("Refresh Token is invalid", ex);
        }

        if (Boolean.TRUE.equals(refreshTokenRes.getIsRevoked()) || refreshTokenRes.getExpiresAt().toInstant().isBefore(Instant.now()))
            throw new TokenNotFound("Refresh Token is invalid");

        refreshTokenRes.setIsRevoked(true);
        this.updateAndCache(refreshToken, refreshTokenRes);

        AuthRequest authRequest = AuthRequest.builder().email(refreshTokenRes.getEmail()).build();
        String newAccessToken = JwtUtils.createAccessToken(authRequest,issuer,Long.parseLong(ttlMillis),secretKey, Constants.Login.ACCESS_TOKEN);
        String newRefreshToken = JwtUtils.createAccessToken(authRequest,issuer,Long.parseLong(refreshTtlMillis),secretKey, Constants.Login.REFRESH_TOKEN);
        long refreshExpiryMillis = System.currentTimeMillis() + Long.parseLong(refreshTtlMillis);
        return TokenResponse.builder().accessToken(newAccessToken).refreshToken(newRefreshToken)
                .refreshTokenExpiresAt(Date.from(Instant.ofEpochMilli(refreshExpiryMillis))).email(refreshTokenRes.getEmail()).build();
    }

     @Cacheable(value = "refreshTokens", key = "#token", unless = "#result == null")
     private Optional<RefreshToken> findByToken(String token) {
        RefreshToken tokenEntity = tokenRepository.findByToken(token).orElse(null);
        return Optional.ofNullable(tokenEntity);
     }

    @CacheEvict(value = "refreshTokens", key = "#token", condition = "#token != null")
    @Cacheable(value = "refreshTokens", key = "#result.token", condition = "#result != null")
    public RefreshToken updateAndCache(String token, RefreshToken refreshToken) {
        return tokenRepository.save(refreshToken);
    }

    @Override
    public String validateEmail(String emailHash) {
        String emailDecrypted = JwtUtils.createHashDecrypt(emailHash, issuer, secretKey);
        return StringUtils.isBlank(emailDecrypted) ? StringUtils.EMPTY : emailDecrypted;
    }

    @Override
    public String createTotp(Integer otpLength, Long timeWindow){
        byte[] k = hexStr2Bytes(Constants.OTP_SECRET); //OTP Secret can be a random number
        byte[] msg = hexStr2Bytes(String.valueOf(timeWindow));
        byte[] hash = applyHmacSha1(k, msg);

        int offset = hash[hash.length-1] & 0xf;

        int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16)
                | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);

        int otp = binary %  ((int)Math.pow(10 , otpLength));
        String result = Integer.toString(otp);
        while (result.length() < otpLength) {
            result = "0".concat(result);
        }
        return result;
    }

    private static byte[] hexStr2Bytes(String hex) {
        // Adding one byte to get the right conversion
        // Values starting with "0" can be converted
        byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();

        // Copy all the REAL bytes, not the "first"
        byte[] ret = new byte[bArray.length - 1];
        System.arraycopy(bArray, 1, ret, 0, ret.length);
        return ret;
    }

    private static byte[] applyHmacSha1(byte[] keyBytes, byte[] text) {
        try {
            Mac mac = Mac.getInstance(Constants.DEFAULT_ALGORITHM);
            SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
            mac.init(macKey);
            return mac.doFinal(text);
        } catch (NoSuchAlgorithmException ex){
            throw new SecurityException("NoSuchAlgorithmException exception", ex);
        } catch (Exception ex){
            throw new SecurityException(ex.getMessage(), ex);
        }
    }
}
