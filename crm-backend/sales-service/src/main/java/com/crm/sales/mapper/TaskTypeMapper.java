package com.crm.sales.mapper;

import com.crm.sales.dto.TaskTypeDto;
import com.crm.sales.entity.TaskType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Task Type Mapper
 * 
 * Custom mapper for converting between TaskType entity and TaskTypeDto.
 * Follows the same pattern as customer-service mappers.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Component
public class TaskTypeMapper {

    /**
     * Convert TaskType entity to TaskTypeDto
     * 
     * @param taskType the task type entity
     * @return the task type DTO
     */
    public TaskTypeDto toDto(TaskType taskType) {
        if (taskType == null) {
            return null;
        }

        TaskTypeDto dto = new TaskTypeDto();
        dto.setId(taskType.getId());
        dto.setName(taskType.getName());
        dto.setCreatedAt(taskType.getCreatedAt());
        dto.setUpdatedAt(taskType.getUpdatedAt());

        return dto;
    }

    /**
     * Convert TaskTypeDto to TaskType entity
     * 
     * @param dto the task type DTO
     * @return the task type entity
     */
    public TaskType toEntity(TaskTypeDto dto) {
        if (dto == null) {
            return null;
        }

        TaskType taskType = new TaskType();
        taskType.setId(dto.getId());
        taskType.setName(dto.getName());

        return taskType;
    }

    /**
     * Convert list of TaskType entities to list of TaskTypeDto
     * 
     * @param taskTypes the list of task type entities
     * @return the list of task type DTOs
     */
    public List<TaskTypeDto> toDtoList(List<TaskType> taskTypes) {
        if (taskTypes == null) {
            return null;
        }

        return taskTypes.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of TaskTypeDto to list of TaskType entities
     * 
     * @param dtos the list of task type DTOs
     * @return the list of task type entities
     */
    public List<TaskType> toEntityList(List<TaskTypeDto> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 