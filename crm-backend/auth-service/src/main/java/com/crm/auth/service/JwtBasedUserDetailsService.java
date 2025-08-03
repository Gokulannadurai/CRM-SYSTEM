package com.crm.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT-based User Details Service
 * 
 * Creates UserDetails from JWT token claims without requiring external service calls.
 * This eliminates the circular dependency issue in microservices.
 * 
 * @version 1.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtBasedUserDetailsService implements UserDetailsService {

    private final JwtService jwtService;

    /**
     * Load user by username from JWT token
     * 
     * @param username the username (extracted from JWT)
     * @return UserDetails object created from JWT claims
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user details from JWT for username: {}", username);
        
        try {
            // Get the current JWT token from the holder
            String jwtToken = getCurrentJwtToken();
            
            if (jwtToken == null) {
                log.warn("No JWT token found in context for username: {}", username);
                throw new UsernameNotFoundException("No JWT token available");
            }

            // Extract user information from JWT token
            String tokenUsername = jwtService.extractUsername(jwtToken);
            Long userId = jwtService.extractUserId(jwtToken);
            List<String> roles = jwtService.extractRoles(jwtToken);
            Boolean isActive = jwtService.extractUserStatus(jwtToken);

            // Validate that the username matches
            if (!username.equals(tokenUsername)) {
                log.warn("Username mismatch: expected {}, got {}", username, tokenUsername);
                throw new UsernameNotFoundException("Username mismatch in JWT token");
            }

            // Validate that user is active
            if (isActive == null || !isActive) {
                log.warn("User is inactive: {}", username);
                throw new UsernameNotFoundException("User is inactive");
            }

            // Convert roles to Spring Security authorities
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());

            log.debug("Successfully created UserDetails for username: {} with roles: {}", 
                    username, roles);

            return User.builder()
                    .username(username)
                    .password("") // Password is not needed for JWT validation
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(!isActive)
                    .build();

        } catch (Exception e) {
            log.error("Error creating UserDetails from JWT for username: {}", username, e);
            throw new UsernameNotFoundException("Error processing JWT token", e);
        }
    }

    /**
     * Get current JWT token from security context
     * 
     * @return JWT token or null if not found
     */
    private String getCurrentJwtToken() {
        return com.crm.auth.util.JwtTokenHolder.getToken();
    }
} 