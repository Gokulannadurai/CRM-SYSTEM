package com.crm.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * User Details Response DTO
 * 
 * Shared DTO for user details response across all microservices.
 * 
 * @version 1.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_nName")
    private String lastName;

    @JsonProperty("mobile_number")
    private String mobileNumber;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    @JsonProperty("lastLoginAt")
    private Date lastLoginAt;

    @JsonProperty("roles")
    private List<RoleDto> roles;

    @JsonProperty("createdAt")
    private Date createdAt;

    @JsonProperty("updatedAt")
    private Date updatedAt;

    /**
     * Role DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleDto {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("level")
        private Integer level;

        @JsonProperty("createdAt")
        private Date createdAt;

        @JsonProperty("updatedAt")
        private Date updatedAt;
    }
} 