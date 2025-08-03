package com.crm.user.service;

import com.crm.user.dto.TokenValidationResult;
import com.crm.user.entity.Role;
import com.crm.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JWT Service
 * 
 * Provides JWT token generation and validation functionality.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshExpiration;

    /**
     * Generate access token for user
     * 
     * @param user the user
     * @return the generated token
     */
    public String generateToken(User user) {
        logger.debug("Generating access token for user: {}", user.getUsername());
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roles", user.getRoles().stream().map(Role::getName)
                .collect(Collectors.joining(", ")));
        claims.put("status", user.getIsActive());
        
        return createToken(claims, user.getUsername(), jwtExpiration);
    }

    /**
     * Generate refresh token for user
     * 
     * @param user the user
     * @return the generated refresh token
     */
    public String generateRefreshToken(User user) {
        logger.debug("Generating refresh token for user: {}", user.getUsername());
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("type", "refresh");
        
        return createToken(claims, user.getUsername(), refreshExpiration);
    }

    /**
     * Create JWT token
     * 
     * @param claims the claims to include
     * @param subject the subject (username)
     * @param expiration the expiration time
     * @return the created token
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate JWT token
     * 
     * @param token the token to validate
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            logger.debug("Validating JWT token");
            
            Claims claims = extractAllClaims(token);
            String username = claims.getSubject();
            Date expiration = claims.getExpiration();
            
            return username != null && !expiration.before(new Date());
            
        } catch (Exception e) {
            logger.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate JWT token with detailed error information
     * 
     * @param token the token to validate
     * @return TokenValidationResult with detailed information
     */
    public TokenValidationResult validateTokenWithDetails(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String username = claims.getSubject();
            Date expiration = claims.getExpiration();
            Date now = new Date();
            
            if (expiration.before(now)) {
                long expiredSeconds = (now.getTime() - expiration.getTime()) / 1000;
                return TokenValidationResult.builder()
                    .valid(false)
                    .errorType("TOKEN_EXPIRED")
                    .errorMessage("Token has expired " + expiredSeconds + " seconds ago")
                    .expiredAt(expiration)
                    .build();
            }
            
            return TokenValidationResult.builder()
                .valid(true)
                .username(username)
                .expiresAt(expiration)
                .build();
                
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.warn("JWT token expired: {}", e.getMessage());
            return TokenValidationResult.builder()
                .valid(false)
                .errorType("TOKEN_EXPIRED")
                .errorMessage("Token has expired")
                .build();
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            logger.warn("Malformed JWT token: {}", e.getMessage());
            return TokenValidationResult.builder()
                .valid(false)
                .errorType("MALFORMED_TOKEN")
                .errorMessage("Token is malformed")
                .build();
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            logger.warn("Unsupported JWT token: {}", e.getMessage());
            return TokenValidationResult.builder()
                .valid(false)
                .errorType("UNSUPPORTED_TOKEN")
                .errorMessage("Token format is not supported")
                .build();
        } catch (io.jsonwebtoken.security.SignatureException e) {
            logger.warn("Invalid JWT signature: {}", e.getMessage());
            return TokenValidationResult.builder()
                .valid(false)
                .errorType("INVALID_SIGNATURE")
                .errorMessage("Token signature is invalid")
                .build();
        } catch (Exception e) {
            logger.warn("Error validating JWT token: {}", e.getMessage());
            return TokenValidationResult.builder()
                .valid(false)
                .errorType("VALIDATION_ERROR")
                .errorMessage("Token validation failed: " + e.getMessage())
                .build();
        }
    }

    /**
     * Extract username from token
     * 
     * @param token the token
     * @return the username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract user ID from token
     * 
     * @param token the token
     * @return the user ID
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extract role from token
     * 
     * @param token the token
     * @return the role
     */
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * Extract expiration date from token
     * 
     * @param token the token
     * @return the expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract claim from token
     * 
     * @param token the token
     * @param claimsResolver the claims resolver function
     * @return the extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     * 
     * @param token the token
     * @return the claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if token is expired
     * 
     * @param token the token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            logger.warn("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Check if token is a refresh token
     * 
     * @param token the token
     * @return true if refresh token, false otherwise
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String type = claims.get("type", String.class);
            return "refresh".equals(type);
        } catch (Exception e) {
            logger.warn("Error checking token type: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get signing key
     * 
     * @return the signing key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
} 