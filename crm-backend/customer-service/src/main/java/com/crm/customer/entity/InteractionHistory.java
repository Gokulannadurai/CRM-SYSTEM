package com.crm.customer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * Interaction History Entity
 * 
 * Represents customer interaction history in the CRM system.
 * Tracks all customer communications and interactions.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "interaction_history", indexes = {
    @Index(name = "idx_interaction_history_customer_id", columnList = "customer_id"),
    @Index(name = "idx_interaction_history_user_id", columnList = "user_id"),
    @Index(name = "idx_interaction_history_interaction_type", columnList = "interaction_type"),
    @Index(name = "idx_interaction_history_interaction_date", columnList = "interaction_date"),
    @Index(name = "idx_interaction_history_created_at", columnList = "created_at")
})
public class InteractionHistory extends BaseEntity {

    @NotNull(message = "Customer ID is required")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "user_id")
    private Long userId;

    @Size(max = 50, message = "Interaction type must not exceed 50 characters")
    @Column(name = "interaction_type")
    private String interactionType;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Temporal(TemporalType.DATE)
    @Column(name = "interaction_date", nullable = false)
    private Date interactionDate;

    /**
     * Default constructor
     */
    public InteractionHistory() {
    }

    /**
     * Constructor with basic interaction information
     * 
     * @param customerId the customer ID
     * @param userId the user ID
     * @param interactionType the type of interaction
     * @param notes the interaction notes
     */
    public InteractionHistory(Long customerId, Long userId, String interactionType, String notes) {
        this.customerId = customerId;
        this.userId = userId;
        this.interactionType = interactionType;
        this.notes = notes;
        this.interactionDate = new Date();
    }
} 