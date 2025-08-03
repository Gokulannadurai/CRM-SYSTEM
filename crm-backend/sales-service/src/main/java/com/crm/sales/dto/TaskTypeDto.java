package com.crm.sales.dto;

import lombok.Data;

import java.util.Date;

/**
 * Task Type DTO
 * 
 * Data Transfer Object for TaskType entity.
 * Used for API requests and responses.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
public class TaskTypeDto {
    private Long id;
    private String name;
    private Date createdAt;
    private Date updatedAt;
} 