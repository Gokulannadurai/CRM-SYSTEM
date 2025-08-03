package com.crm.sales.dto;

import lombok.Data;
import java.util.Date;

/**
 * Task Summary DTO
 * 
 * Simplified task data for inclusion in lead details.
 * Contains essential task information without full details.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
public class TaskSummaryDto {
    private Long id;
    private String title;
    private String description;
    private Long taskTypeId;
    private String taskTypeName;
    private Long assignedTo;
    private String assignedUserName;
    private Date dueDate;
    private Boolean isCompleted;
    private Boolean isStarted;
    private String status;
    private Date createdAt;
} 