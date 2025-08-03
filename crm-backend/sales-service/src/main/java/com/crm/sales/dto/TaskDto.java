package com.crm.sales.dto;

import com.crm.sales.entity.Attachment;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * Task DTO
 * 
 * Data Transfer Object for Task entity.
 * Used for API requests and responses.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
public class TaskDto {
    private Long id;
    private Long leadId;
    private Long customerId;
    private Long assignedTo;
    private Long taskTypeId;
    private String title;
    private String description;
    private Date dueDate;
    private Boolean isCompleted;
    private Boolean isStarted;
    private List<MultipartFile> files;
    private Date createdAt;
    private Date updatedAt;
    private List<AttachmentDTO> attachments;
    
    // Related lead data
    private LeadSummaryDto relatedLead;
} 