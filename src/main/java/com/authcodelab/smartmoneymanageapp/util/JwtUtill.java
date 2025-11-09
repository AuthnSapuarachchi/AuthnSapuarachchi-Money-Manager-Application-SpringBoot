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

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
@Slf4j
public class JwtUtill {

    @Value("${jwt.secret:YXV0aG5tb25leW1hbmFnZWFwcGF1dGhubW9uZXltYW5hZ2VhcHBzZWNyZXRrZXk=}")
    private String SECRET_KEY;

    @Value("${jwt.expiration:36000000}") // Default: 10 hours in milliseconds
    private Long jwtExpirationMs;

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    public String generateToken(Map<String, Object> extraClaims, String email) {
        return createToken(extraClaims, email);
    }

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


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    public String extractEmail(String token) {
        return extractUsername(token);
    }


    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


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


    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }


    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }


    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    public Boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }


    private Key getSignKey() {
        try {
            byte[] keyBytes;
            // Support either explicit base64 (prefix "base64:") or raw base64 or plain-text secret.
            if (SECRET_KEY == null || SECRET_KEY.trim().isEmpty()) {
                throw new IllegalStateException("JWT SECRET_KEY is not configured");
            }

            if (SECRET_KEY.startsWith("base64:")) {
                String payload = SECRET_KEY.substring("base64:".length());
                keyBytes = Decoders.BASE64.decode(payload);
                log.debug("Using base64 secret (prefixed). key bytes: {}", keyBytes.length);
            } else {
                // First try to decode as base64; if it fails, treat as raw text
                try {
                    keyBytes = Decoders.BASE64.decode(SECRET_KEY);
                    log.debug("Using base64 secret. key bytes: {}", keyBytes.length);
                } catch (IllegalArgumentException ex) {
                    keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
                    log.debug("Using plain-text secret. key bytes: {}", keyBytes.length);
                }
            }

            if (keyBytes.length < 32) {
                // HS256 expects a 256-bit key (32 bytes) for optimal security. We allow smaller keys but log a warning.
                log.warn("JWT signing key is less than 256 bits ({} bytes). This may cause security or compatibility issues with HS256. Consider using a 32-byte (or longer) secret.", keyBytes.length);
            }

            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Failed to construct signing key from SECRET_KEY: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT secret configuration", e);
        }
    }


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
