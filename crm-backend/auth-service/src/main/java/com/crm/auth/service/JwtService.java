package com.crm.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Shared JWT Service
 * 
 * Provides JWT token operations for all microservices.
 * 
 * @version 1.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret:crm-jwt-secret-key-2025-user-service}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    @Value("${jwt.refresh-token.expiration:86400000}")
    private long refreshExpiration;

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
     * Extract roles from token
     * 
     * @param token the token
     * @return list of roles
     */
    public List<String> extractRoles(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String rolesString = claims.get("roles", String.class);
            if (rolesString != null && !rolesString.trim().isEmpty()) {
                return Arrays.asList(rolesString.split(",\\s*"));
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.warn("Error extracting roles from token: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Extract user ID from token
     * 
     * @param token the token
     * @return the user ID
     */
    public Long extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            log.warn("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract user status from token
     * 
     * @param token the token
     * @return the user status
     */
    public Boolean extractUserStatus(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("status", Boolean.class);
        } catch (Exception e) {
            log.warn("Error extracting user status from token: {}", e.getMessage());
            return false;
        }
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
     * Get signing key
     * 
     * @return the signing key
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Check if token is expired
     * 
     * @param token the token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate JWT token against user details
     * 
     * @param token the JWT token
     * @param userDetails the user details
     * @return true if valid, false otherwise
     */
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Error validating JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate JWT token without user details (for token validation only)
     * 
     * @param token the JWT token
     * @return true if valid, false otherwise
     */
    public Boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Error validating JWT token: {}", e.getMessage());
            return false;
        }
    }


} 