package com.crm.sales.dto;

import lombok.Data;

import java.util.Date;

/**
 * Lead Stage DTO
 * 
 * Data Transfer Object for LeadStage entity.
 * Used for API requests and responses.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
public class LeadStageDto {
    private Long id;
    private String name;
    private Integer position;
    private Date createdAt;
    private Date updatedAt;
} 