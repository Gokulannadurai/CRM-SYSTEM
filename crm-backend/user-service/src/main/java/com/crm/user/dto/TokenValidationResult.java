package com.crm.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Token Validation Result DTO
 * 
 * Provides detailed information about token validation results including error details.
 * 
 * @version 1.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResult {

    @JsonProperty("valid")
    private Boolean valid;

    @JsonProperty("username")
    private String username;

    @JsonProperty("expiresAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expiresAt;

    @JsonProperty("expiredAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expiredAt;

    @JsonProperty("errorType")
    private String errorType;

    @JsonProperty("errorMessage")
    private String errorMessage;

    @JsonProperty("errorCode")
    private String errorCode;

    /**
     * Get error code based on error type
     * 
     * @return the error code
     */
    public String getErrorCode() {
        if (errorType == null) {
            return null;
        }
        
        switch (errorType) {
            case "TOKEN_EXPIRED":
                return "AUTH_001";
            case "MALFORMED_TOKEN":
                return "AUTH_002";
            case "UNSUPPORTED_TOKEN":
                return "AUTH_003";
            case "INVALID_SIGNATURE":
                return "AUTH_004";
            case "VALIDATION_ERROR":
                return "AUTH_005";
            default:
                return "AUTH_000";
        }
    }
} 