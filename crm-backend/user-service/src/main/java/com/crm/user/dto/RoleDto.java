package com.crm.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

/**
 * Role DTO
 * 
 * Data Transfer Object for Role entity.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@Data
public class RoleDto {

    @JsonProperty("id")
    private Long id;

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
    @JsonProperty("name")
    private String name;

    @NotNull(message = "Role level is required")
    private Integer level;

    private Date createdAt;
    private Date updatedAt;

    /**
     * Default constructor
     */
    public RoleDto() {
    }

    public RoleDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor with all fields
     * 
     * @param id the role ID
     * @param name the role name
     * @param level the role level
     * @param createdAt the creation timestamp
     * @param updatedAt the update timestamp
     */
    public RoleDto(Long id, String name, Integer level, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
} 