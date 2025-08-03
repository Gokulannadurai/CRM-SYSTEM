package com.crm.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token Validation Response DTO
 * 
 * Shared DTO for token validation response across all microservices.
 * 
 * @version 1.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {

    @JsonProperty("valid")
    private Boolean valid;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("roles")
    private String roles;

    @JsonProperty("message")
    private String message;

    @JsonProperty("expiresAt")
    private Long expiresAt;
} 