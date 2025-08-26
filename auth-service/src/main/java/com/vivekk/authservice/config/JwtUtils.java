package com.vivekk.authservice.config;

import com.vivekk.authservice.dto.AuthRequest;
import com.vivekk.authservice.utils.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
public class JwtUtils {

    public static String createAccessToken(AuthRequest authRequest, String issuer,  Long ttlMillis , String secretKey, String type){
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] secretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(secretBytes, signatureAlgorithm.getJcaName());
        String email = authRequest.getEmail();
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("scopes", getTokenScope(type));
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date date = new Date(expMillis);
        return Jwts.builder().signWith(signingKey).setClaims(claims).setIssuer(issuer).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(date).setId(UUID.randomUUID().toString()).compact();
    }

    static List<String> getTokenScope(String type){
        if (type.equalsIgnoreCase(Constants.Login.ACCESS_TOKEN)){
            List<String> scopeList = new ArrayList<>();
            scopeList.add("Read");
            scopeList.add("Write");
            return scopeList;
        }else {
            List<String> scopeList = new ArrayList<>();
            scopeList.add("refresh_token");
            return scopeList;
        }
    }

    public static String createHashToken(AuthRequest authRequest, String issuer, String secretKey) {
        try {
            byte[] salt = issuer.getBytes(StandardCharsets.UTF_8);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(("PBKDF2WithHmacSHA256"));
            PBEKeySpec pbeKeySpec = new PBEKeySpec(secretKey.toCharArray(), salt , 12000, 256);
            SecretKey secretKey1 = keyFactory.generateSecret(pbeKeySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey1.getEncoded(), "AES");
            return encryptEmail(authRequest.getEmail(), secretKeySpec);
        }catch (GeneralSecurityException exception){
            log.info("Exception occurred while creating HashToken");
            throw new SecurityException(exception.getMessage(), exception.getCause());
        }
    }

    private static String encryptEmail(String password, SecretKeySpec secretKeySpec) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)));
        }catch (Exception ex){
            log.info("Exception occurred while encrypting email");
            throw ex;
        }
    }

    public static String createHashDecrypt(String encryptedEmail, String issuer, String secretKey) {
        try {
            byte[] salt = issuer.getBytes(StandardCharsets.UTF_8);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(("PBKDF2WithHmacSHA256"));
            PBEKeySpec pbeKeySpec = new PBEKeySpec(secretKey.toCharArray(), salt , 12000, 256);
            SecretKey secretKey1 = keyFactory.generateSecret(pbeKeySpec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey1.getEncoded(), "AES");
            return decryptEmail(encryptedEmail, secretKeySpec);
        }catch (GeneralSecurityException exception){
            log.info("Exception occurred while decrypting HashToken");
            throw new SecurityException(exception.getMessage(), exception.getCause());
        }
    }

    private static String decryptEmail(String encryptedEmail, SecretKey secretKey) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encryptedEmailBytes  =  Base64.getDecoder().decode(encryptedEmail);
            byte[] decryptedBytes = cipher.doFinal(encryptedEmailBytes);
            return new String(decryptedBytes);
        }catch (Exception ex){
            log.info("Exception occurred while decrypting email");
            throw ex;
        }
    }

    public static boolean validateToken(String token, String secretKey, String issuer){
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)).build();
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        if (!issuer.equals(claims.getIssuer())) {
            return false;
        }
        return claims.getExpiration().after(java.util.Date.from(Instant.now()));
    }
}
