package com.mapago.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public static final String ADMIN_SECRET_KEY = Base64.getEncoder()
            .encodeToString("abcdefghijklmnopqrstuvwx123456789012abcdefghijklmnopqrstuvwx123456789012".getBytes());
    public static final String ADMIN_REFRESH_SECRET_KEY = Base64.getEncoder()
            .encodeToString("mnopqrstuvwxyzabcdef123456789012mnopqrstuvwxyzabcdef123456789012".getBytes());
    public static final String PORTAL_SECRET_KEY = Base64.getEncoder()
            .encodeToString("1234567890abcdefghijklmnopqrstuvwx1234567890abcdefghijklmnopqrstuvwx".getBytes());
    public static final String PORTAL_REFRESH_SECRET_KEY = Base64.getEncoder()
            .encodeToString("1234567890mnopqrstuvwxyzabcdef1234567890mnopqrstuvwxyzabcdef".getBytes());

    /**
     * 어드민 START
     **/
    public String generateAdminAccessToken(String userId) {
        return createToken(new HashMap<>(), userId, ADMIN_SECRET_KEY, 1000 * 60 * 60 * 24 * 60); // accessToken: 60일 유효
    }

    public String generateAdminRefreshToken(String userId) {
        return createToken(new HashMap<>(), userId, ADMIN_REFRESH_SECRET_KEY,
                1000 * 60 * 60 * 24 * 7);
    }

    public Boolean validateAdminAccessToken(String token, String userId) {
        return validateToken(token, userId, ADMIN_SECRET_KEY);
    }

    public Boolean validateAdminRefreshToken(String token, String userId) {
        return validateToken(token, userId, ADMIN_REFRESH_SECRET_KEY);
    }
    /** 어드민 END **/


    /**
     * 포털 START
     **/
    public String generatePortalAccessToken(String userId) {
        return createToken(new HashMap<>(), userId, PORTAL_SECRET_KEY, 1000 * 60 * 60 * 24 * 15); // accessToken: 15일 유효
    }

    public String generatePortalRefreshToken(String userId) {
        return createToken(new HashMap<>(), userId, PORTAL_REFRESH_SECRET_KEY,
                1000 * 60 * 60 * 24 * 7); // refreshToken: 7일 유효
    }

    public Boolean validatePortalAccessToken(String token, String userId) {
        return validateToken(token, userId, PORTAL_SECRET_KEY);
    }

    public Boolean validatePortalRefreshToken(String token, String userId) {
        return validateToken(token, userId, PORTAL_REFRESH_SECRET_KEY);
    }
    /** 포털 END **/


    /**
     * 공통 START
     **/
    public Boolean validateToken(String token, String userId, String secretKey) {
        final String extractedUserId = extractUserId(token, secretKey);
        return (extractedUserId.equals(userId) && !isTokenExpired(token, secretKey));
    }

    public Claims extractAllClaims(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public String extractUserId(String token, String secretKey) {
        return extractClaim(token, Claims::getSubject, secretKey);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = extractAllClaims(token, secretKey);
        return claimsResolver.apply(claims);
    }

    public Boolean isTokenExpired(String token, String secretKey) {
        return extractExpiration(token, secretKey).before(new Date());
    }

    public Date extractExpiration(String token, String secretKey) {
        return extractClaim(token, Claims::getExpiration, secretKey);
    }

    public String createToken(Map<String, Object> claims, String subject, String secretKey, long expirationTime) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }
    /** 공통 END **/
}