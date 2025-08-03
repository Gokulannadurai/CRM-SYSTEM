package com.crm.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Login Response DTO
 * 
 * DTO for user login responses.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@Data
public class LoginResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("email")
    private String email;

    @JsonProperty("roles")
    private String roles;

    @JsonProperty("message")
    private String message;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(String accessToken, String refreshToken, Long expiresIn, String email, String roles, String message) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.email = email;
        this.roles = roles;
        this.message = message;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "accessToken='[PROTECTED]'" +
                ", refreshToken='[PROTECTED]'" +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", email=" + email +
                ", roles=" + roles +
                ", message='" + message + '\'' +
                '}';
    }
} 