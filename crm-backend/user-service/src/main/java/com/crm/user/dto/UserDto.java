package com.crm.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * User Data Transfer Object
 * 
 * DTO for transferring user data between layers.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@Data
public class UserDto {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("username")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @JsonProperty("first_name")
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;

    @JsonProperty("roles")
    private List<RoleDto> roles;

    @JsonProperty("last_name")
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("mobile_number")
    @Size(max = 20, message = "Mobile number must not exceed 20 characters")
    private String mobileNumber;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    private Date lastLoginAt;

    private Date createdAt;

    private Date updatedAt;

    // Constructors
    public UserDto() {}

    @Override
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + roles +
                ", status=" +  isActive +
                ", lastLoginAt=" + lastLoginAt +
                ", createdAt=" + createdAt +
                '}';
    }
} 