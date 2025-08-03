package com.crm.sales.mapper;

import com.crm.sales.dto.TaskDto;
import com.crm.sales.dto.LeadSummaryDto;
import com.crm.sales.entity.Task;
import com.crm.sales.entity.Lead;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Task Mapper
 * 
 * Custom mapper for converting between Task entity and TaskDto.
 * Follows the same pattern as customer-service mappers.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Component
public class TaskMapper {

    /**
     * Convert Task entity to TaskDto
     * 
     * @param task the task entity
     * @return the task DTO
     */
    public TaskDto toDto(Task task) {
        if (task == null) {
            return null;
        }

        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setLeadId(task.getLeadId());
        dto.setCustomerId(task.getCustomerId());
        dto.setAssignedTo(task.getAssignedTo());
        dto.setTaskTypeId(task.getTaskTypeId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setIsCompleted(task.getIsCompleted());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        return dto;
    }

    /**
     * Convert Task entity to TaskDto with related lead
     * 
     * @param task the task entity
     * @param relatedLead the related lead entity
     * @return the task DTO with related lead
     */
    public TaskDto toDtoWithLead(Task task, Lead relatedLead) {
        TaskDto dto = toDto(task);
        if (relatedLead != null) {
            LeadSummaryDto leadSummary = toLeadSummaryDto(relatedLead);
            dto.setRelatedLead(leadSummary);
        }
        return dto;
    }

    /**
     * Convert Lead entity to LeadSummaryDto
     * 
     * @param lead the lead entity
     * @return the lead summary DTO
     */
    public LeadSummaryDto toLeadSummaryDto(Lead lead) {
        if (lead == null) {
            return null;
        }

        LeadSummaryDto dto = new LeadSummaryDto();
        dto.setId(lead.getId());
        dto.setTitle(lead.getTitle());
        dto.setCustomerId(lead.getCustomerId());
        dto.setCustomerName(lead.getCustomerName());
        dto.setStatus(lead.getStatus());
        dto.setExpectedCloseDate(lead.getExpectedCloseDate());
        dto.setValue(lead.getValue());
        dto.setCreatedAt(lead.getCreatedAt());
        
        return dto;
    }

    /**
     * Convert TaskDto to Task entity
     * 
     * @param dto the task DTO
     * @return the task entity
     */
    public Task toEntity(TaskDto dto) {
        if (dto == null) {
            return null;
        }

        Task task = new Task();
        task.setId(dto.getId());
        task.setLeadId(dto.getLeadId());
        task.setCustomerId(dto.getCustomerId());
        task.setAssignedTo(dto.getAssignedTo());
        task.setTaskTypeId(dto.getTaskTypeId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setIsCompleted(dto.getIsCompleted());

        return task;
    }

    /**
     * Convert list of Task entities to list of TaskDto
     * 
     * @param tasks the list of task entities
     * @return the list of task DTOs
     */
    public List<TaskDto> toDtoList(List<Task> tasks) {
        if (tasks == null) {
            return null;
        }

        return tasks.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of TaskDto to list of Task entities
     * 
     * @param dtos the list of task DTOs
     * @return the list of task entities
     */
    public List<Task> toEntityList(List<TaskDto> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 