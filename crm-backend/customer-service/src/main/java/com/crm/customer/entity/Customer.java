package com.crm.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Customer Entity
 * 
 * Represents a customer in the CRM system with basic profile information.
 * Extends BaseEntity for common fields and behavior.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "customer", indexes = {
    @Index(name = "idx_customer_email", columnList = "email"),
    @Index(name = "idx_customer_company", columnList = "company"),
    @Index(name = "idx_customer_source", columnList = "source"),
    @Index(name = "idx_customer_created_by", columnList = "created_by"),
    @Index(name = "idx_customer_is_active", columnList = "is_active"),
    @Index(name = "idx_customer_created_at", columnList = "created_at")
})
public class Customer extends BaseEntity {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @Email(message = "Email must be a valid email address")
    @Column(name = "email", unique = true)
    private String email;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Column(name = "phone")
    private String phone;

    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @Column(name = "company")
    private String company;

    @Size(max = 100, message = "Source must not exceed 100 characters")
    @Column(name = "source")
    private String source;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description")
    private String description;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Default constructor
     */
    public Customer() {
    }

    /**
     * Constructor with basic customer information
     * 
     * @param name the customer name
     * @param email the email address
     * @param phone the phone number
     * @param company the company name
     * @param source the customer source
     */
    public Customer(String name, String email, String phone, String company, String source) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.source = source;
    }
} 