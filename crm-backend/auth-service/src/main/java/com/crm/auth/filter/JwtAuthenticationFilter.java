package com.crm.auth.filter;

import com.crm.auth.service.JwtService;
import com.crm.auth.util.JwtTokenHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Shared JWT Authentication Filter
 * 
 * Filters incoming requests to validate JWT tokens and set Spring Security context.
 * Creates UserDetails from JWT claims to eliminate external service dependencies.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        log.debug("JWT Authentication Filter processing request: {} {}", request.getMethod(), request.getRequestURI());

        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String username;

            log.debug("Authorization header: {}", authHeader != null ? "present" : "null");
            if (authHeader != null) {
                log.debug("Authorization header value: '{}'", authHeader);
                log.debug("Authorization header starts with 'Bearer ': {}", authHeader.startsWith("Bearer "));
            }

            // Check if Authorization header exists and starts with "Bearer "
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("No valid Authorization header found for request: {} {}", request.getMethod(), request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            // Extract JWT token from Authorization header
            jwt = authHeader.substring(7);
            log.debug("JWT token extracted from Authorization header, length: {}", jwt.length());

            // Check if token is expired first
            if (jwtService.isTokenExpired(jwt)) {
                log.warn("JWT token has expired for request: {} {}", request.getMethod(), request.getRequestURI());
                sendExpiredTokenError(response);
                return;
            }

            // Store token in holder for UserDetailsService to access
            JwtTokenHolder.setToken(jwt);
            
            // Extract username from JWT token
            username = jwtService.extractUsername(jwt);
            log.debug("Username extracted from JWT: {} for request: {} {}", username, request.getMethod(), request.getRequestURI());

            // If username is extracted and no authentication is set in context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.debug("Setting authentication for user: {} for request: {} {}", username, request.getMethod(), request.getRequestURI());

                try {
                    // Load user details from JWT-based service
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    log.debug("User details loaded for user: {} with authorities: {}", username, userDetails.getAuthorities());

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    // Set authentication details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authentication set in security context for user: {} with roles: {} for request: {} {}", 
                            username, userDetails.getAuthorities(), request.getMethod(), request.getRequestURI());
                            
                } catch (Exception e) {
                    log.warn("Failed to load user details for username: {} for request: {} {}", username, request.getMethod(), request.getRequestURI(), e);
                    // Continue with filter chain - let Spring Security handle the unauthenticated request
                }
            } else if (username == null) {
                log.warn("Could not extract username from JWT token for request: {} {}", request.getMethod(), request.getRequestURI());
            } else {
                log.debug("Authentication already set in context for user: {} for request: {} {}", username, request.getMethod(), request.getRequestURI());
            }

        } catch (Exception e) {
            log.error("Error processing JWT authentication for request: {} {}", request.getMethod(), request.getRequestURI(), e);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            JwtTokenHolder.clear();
        }
    }

    /**
     * Send expired token error response
     * 
     * @param response the HTTP response
     */
    private void sendExpiredTokenError(HttpServletResponse response) {
        try {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            // Create simple error response for frontend to handle
            ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(new java.util.Date())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("TOKEN_EXPIRED")
                .message("Token has expired. Please login again.")
                .errorCode("AUTH_001")
                .errorType("TOKEN_EXPIRED")
                .path("/api/v1/auth/validate")
                .build();
            
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
            
        } catch (Exception e) {
            log.error("Error sending expired token error response", e);
        }
    }
}

/**
 * Error Response DTO for token validation errors
 */
@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
class ErrorResponse {
    private java.util.Date timestamp;
    private int status;
    private String error;
    private String message;
    private String errorCode;
    private String errorType;
    private String path;
} 