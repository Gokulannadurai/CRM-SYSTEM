package com.crm.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

/**
 * Interaction History Data Transfer Object
 * 
 * DTO for transferring interaction history data between layers.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
public class InteractionHistoryDto {

    private Long id;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private Long userId;

    @Size(max = 50, message = "Interaction type must not exceed 50 characters")
    private String interactionType;

    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date interactionDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;
} 