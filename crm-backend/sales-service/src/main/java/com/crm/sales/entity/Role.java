package com.crm.sales.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Role Entity
 * 
 * Represents a role in the CRM system with access hierarchy levels.
 * Extends BaseEntity for common fields and behavior.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "role")
public class Role extends BaseEntity {

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @NotNull(message = "Role level is required")
    @Column(name = "level", nullable = false)
    private Integer level;

    /**
     * Default constructor
     */
    public Role(Long id, String name) {
        super(id);
        this.name = name;
    }

    public Role() {
        super();
    }
} 