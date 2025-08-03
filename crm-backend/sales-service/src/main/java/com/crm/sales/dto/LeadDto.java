package com.crm.sales.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Date;
import java.util.List;

/**
 * Lead DTO
 * 
 * Data Transfer Object for Lead entity.
 * Used for API requests and responses.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
public class LeadDto {
    private String title;
    private Long id;
    private Long customerId;
    private String customerName;
    private Long assignedTo;
    private String userName;
    private String status;
    private Long currentStageId;
    private Date expectedCloseDate;
    private BigDecimal value;
    private String additionalNotes;
    private List<MultipartFile> files;
    private List<AttachmentDTO> attachments;
    private Date createdAt;
    private Date updatedAt;
    
    // Related tasks data
    private List<TaskSummaryDto> relatedTasks;
} 