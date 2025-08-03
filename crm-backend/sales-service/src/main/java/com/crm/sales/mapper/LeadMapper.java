package com.crm.sales.mapper;

import com.crm.sales.dto.LeadDto;
import com.crm.sales.dto.TaskSummaryDto;
import com.crm.sales.entity.Lead;
import com.crm.sales.entity.Task;
import com.crm.sales.entity.User;
import com.crm.sales.entity.TaskType;
import com.crm.sales.repository.UserRepository;
import com.crm.sales.repository.TaskTypeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Lead Mapper
 * 
 * Custom mapper for converting between Lead entity and LeadDto.
 * Follows the same pattern as customer-service mappers.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Component
public class LeadMapper {

    private final UserRepository userRepository;
    private final TaskTypeRepository taskTypeRepository;

    public LeadMapper(UserRepository userRepository, TaskTypeRepository taskTypeRepository) {
        this.userRepository = userRepository;
        this.taskTypeRepository = taskTypeRepository;
    }

    /**
     * Convert Lead entity to LeadDto
     * 
     * @param lead the lead entity
     * @return the lead DTO
     */
    public LeadDto toDto(Lead lead) {
        if (lead == null) {
            return null;
        }

        LeadDto dto = new LeadDto();
        dto.setId(lead.getId());
        dto.setCustomerId(lead.getCustomerId());
        dto.setCustomerName(lead.getCustomerName());
        dto.setAssignedTo(lead.getAssignedTo());
        dto.setUserName(lead.getUserName());
        dto.setStatus(lead.getStatus());
        dto.setCurrentStageId(lead.getCurrentStageId());
        dto.setExpectedCloseDate(lead.getExpectedCloseDate());
        dto.setValue(lead.getValue());
        dto.setCreatedAt(lead.getCreatedAt());
        dto.setUpdatedAt(lead.getUpdatedAt());
        dto.setTitle(lead.getTitle());
        dto.setAdditionalNotes(lead.getAdditionalNotes());
        return dto;
    }

    /**
     * Convert Lead entity to LeadDto with related tasks
     * 
     * @param lead the lead entity
     * @param relatedTasks list of related tasks
     * @return the lead DTO with related tasks
     */
    public LeadDto toDtoWithTasks(Lead lead, List<Task> relatedTasks) {
        LeadDto dto = toDto(lead);
        if (relatedTasks != null && !relatedTasks.isEmpty()) {
            List<TaskSummaryDto> taskSummaries = relatedTasks.stream()
                    .map(this::toTaskSummaryDto)
                    .collect(Collectors.toList());
            dto.setRelatedTasks(taskSummaries);
        }
        return dto;
    }

    /**
     * Convert Task entity to TaskSummaryDto
     * 
     * @param task the task entity
     * @return the task summary DTO
     */
    public TaskSummaryDto toTaskSummaryDto(Task task) {
        if (task == null) {
            return null;
        }

        TaskSummaryDto dto = new TaskSummaryDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setTaskTypeId(task.getTaskTypeId());
        dto.setAssignedTo(task.getAssignedTo());
        dto.setDueDate(task.getDueDate());
        dto.setIsCompleted(task.getIsCompleted());
        dto.setIsStarted(task.getIsStarted());
        dto.setCreatedAt(task.getCreatedAt());
        
        // Fetch and set task type name
        if (task.getTaskTypeId() != null) {
            TaskType taskType = taskTypeRepository.findById(task.getTaskTypeId()).orElse(null);
            if (taskType != null) {
                dto.setTaskTypeName(taskType.getName());
            }
        }
        
        // Fetch and set assigned user name
        if (task.getAssignedTo() != null) {
            User assignedUser = userRepository.findById(task.getAssignedTo()).orElse(null);
            if (assignedUser != null) {
                dto.setAssignedUserName(assignedUser.getFullName());
            }
        }
        
        // Set status based on completion
        if (task.getIsCompleted()) {
            dto.setStatus("COMPLETED");
        } else if (task.getIsStarted()) {
            dto.setStatus("IN_PROGRESS");
        } else {
            dto.setStatus("PENDING");
        }
        
        return dto;
    }

    /**
     * Convert LeadDto to Lead entity
     * 
     * @param dto the lead DTO
     * @return the lead entity
     */
    public Lead toEntity(LeadDto dto) {
        if (dto == null) {
            return null;
        }

        Lead lead = new Lead();
        lead.setId(dto.getId());
        lead.setCustomerId(dto.getCustomerId());
        lead.setCustomerName(dto.getCustomerName());
        lead.setAssignedTo(dto.getAssignedTo());
        lead.setUserName(dto.getUserName());
        lead.setStatus(dto.getStatus());
        lead.setCurrentStageId(dto.getCurrentStageId());
        lead.setExpectedCloseDate(dto.getExpectedCloseDate());
        lead.setValue(dto.getValue());
        lead.setTitle(dto.getTitle());
        lead.setAdditionalNotes(dto.getAdditionalNotes());
        return lead;
    }

    /**
     * Convert list of Lead entities to list of LeadDto
     * 
     * @param leads the list of lead entities
     * @return the list of lead DTOs
     */
    public List<LeadDto> toDtoList(List<Lead> leads) {
        if (leads == null) {
            return null;
        }

        return leads.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of LeadDto to list of Lead entities
     * 
     * @param dtos the list of lead DTOs
     * @return the list of lead entities
     */
    public List<Lead> toEntityList(List<LeadDto> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 