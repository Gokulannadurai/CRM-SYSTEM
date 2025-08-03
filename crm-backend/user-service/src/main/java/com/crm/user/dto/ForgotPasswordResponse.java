package com.crm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Forgot Password Response DTO
 * 
 * Data Transfer Object for forgot password responses.
 * Used to return the status of password reset requests.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordResponse {
    
    private boolean success;
    private String message;
    private String email;
} 