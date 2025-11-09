package com.authcodelab.smartmoneymanageapp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Utility class for Money Management Application
 * Handles JWT token generation, validation, and extraction of claims
 */
@Component
@Slf4j
public class JwtUtill {

    @Value("${jwt.secret:YXV0aG5tb25leW1hbmFnZWFwcGF1dGhubW9uZXltYW5hZ2VhcHBzZWNyZXRrZXk=}")
    private String SECRET_KEY;

    @Value("${jwt.expiration:36000000}") // Default: 10 hours in milliseconds
    private Long jwtExpirationMs;

    /**
     * Generate a JWT token for the given email
     * 
     * @param email User's email address
     * @return Generated JWT token
     */
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    /**
     * Generate a JWT token with custom claims
     * 
     * @param extraClaims Additional claims to include in the token
     * @param email       User's email address
     * @return Generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, String email) {
        return createToken(extraClaims, email);
    }

    /**
     * Create the JWT token with claims, subject, issued time, expiration, and
     * signature
     * 
     * @param claims  Additional claims to include
     * @param subject The subject (email) of the token
     * @return JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(currentTimeMillis + jwtExpirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract username (email) from the token
     * 
     * @param token JWT token
     * @return Email address from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract email from the token (alias for extractUsername)
     * 
     * @param token JWT token
     * @return Email address from token
     */
    public String extractEmail(String token) {
        return extractUsername(token);
    }

    /**
     * Extract expiration date from token
     * 
     * @param token JWT token
     * @return Expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract issued at date from token
     * 
     * @param token JWT token
     * @return Issued at date
     */
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    /**
     * Extract any claim using a resolver function
     * 
     * @param token          JWT token
     * @param claimsResolver Function to resolve specific claim
     * @return Resolved claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parse the JWT token to extract all claims
     * 
     * @param token JWT token
     * @return Claims object containing all claims
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Check if the token has expired
     * 
     * @param token JWT token
     * @return true if token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Validate token against username and expiration
     * 
     * @param token    JWT token
     * @param username Username to validate against
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate token against UserDetails
     * 
     * @param token       JWT token
     * @param userDetails UserDetails object
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Check if token is valid (not expired and properly formatted)
     * 
     * @param token JWT token
     * @return true if token is valid, false otherwise
     */
    public Boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the signing key from secret
     * 
     * @return Key object for signing/verifying tokens
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Get remaining time until token expiration in milliseconds
     * 
     * @param token JWT token
     * @return Remaining time in milliseconds, or 0 if expired
     */
    public Long getRemainingExpirationTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return remaining > 0 ? remaining : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}
