package com.crm.auth.client;

import com.crm.auth.dto.UserDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Shared Feign Client for User Service Communication
 * 
 * Declarative REST client for communicating with user-service.
 * Provides type-safe API calls with automatic serialization/deserialization.
 * 
 * @version 1.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@FeignClient(
    name = "userservice",
    url = "${user.service.url}")
public interface UserServiceClient {

    /**
     * Get user details by username
     * 
     * @param username the username
     * @param authorization the JWT token
     * @return user details response
     */
    @GetMapping("/api/v1/users/username/{username}")
    ResponseEntity<UserDetailsResponse> getUserByUsername(
            @PathVariable("username") String username,
            @RequestHeader("Authorization") String authorization
    );

    /**
     * Get user details by username (without authorization header for internal calls)
     * 
     * @param username the username
     * @return user details response
     */
    @GetMapping("/api/v1/users/username/{username}")
    ResponseEntity<UserDetailsResponse> getUserByUsername(
            @PathVariable("username") String username
    );


    /**
     * Get user by ID
     * 
     * @param userId the user ID
     * @param authorization the JWT token
     * @return user details response
     */
    @GetMapping("/api/v1/users/{userId}")
    ResponseEntity<UserDetailsResponse> getUserById(
            @PathVariable("userId") Long userId,
            @RequestHeader("Authorization") String authorization
    );

    /**
     * Health check endpoint
     * 
     * @return health status
     */
    @GetMapping("/actuator/health")
    ResponseEntity<String> healthCheck();
} 