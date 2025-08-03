package com.crm.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * User Entity
 * 
 * Represents a user in the CRM system with basic authentication and profile information.
 * Extends BaseEntity for common fields and behavior.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "\"user\"")
public class User extends BaseEntity {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Size(max = 20, message = "Mobile number must not exceed 20 characters")
    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Temporal(TemporalType.DATE)
    @Column(name = "last_login_at")
    private Date lastLoginAt;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Constructor with basic user information
     * 
     * @param username the username
     * @param email the email address
     * @param password the hashed password
     * @param firstName the first name
     * @param lastName the last name
     * @param mobileNumber the mobile number
     */
    public User(String username, String email, String password,
                String firstName, String lastName, String mobileNumber) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
    }

    /**
     * Get the full name of the user
     * 
     * @return the full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Check if the user is active
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive) && !Boolean.TRUE.equals(isDeleted);
    }

    /**
     * Check if user has a specific role
     * 
     * @param roleName the role name to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(role -> role.getName().equals(roleName));
    }

    /**
     * Check if user has admin role
     * 
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
} 