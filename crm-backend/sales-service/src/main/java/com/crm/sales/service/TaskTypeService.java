package com.crm.sales.service;

import com.crm.sales.dto.TaskTypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Task Type Service Interface
 * 
 * Service interface for TaskType business operations.
 * Defines methods for task type management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
public interface TaskTypeService {

    /**
     * Create a new task type
     * 
     * @param taskTypeDto the task type data
     * @return the created task type
     */
    TaskTypeDto createTaskType(TaskTypeDto taskTypeDto);

    /**
     * Get task type by ID
     * 
     * @param id the task type ID
     * @return the task type
     */
    TaskTypeDto getTaskTypeById(Long id);

    /**
     * Get task type by name
     * 
     * @param name the task type name
     * @return the task type
     */
    TaskTypeDto getTaskTypeByName(String name);

    /**
     * Get all task types
     * 
     * @return list of task types
     */
    List<TaskTypeDto> getAllTaskTypes();

    /**
     * Get all task types with pagination
     * 
     * @param pageable pagination parameters
     * @return page of task types
     */
    Page<TaskTypeDto> getAllTaskTypes(Pageable pageable);

    /**
     * Update task type
     * 
     * @param id the task type ID
     * @param taskTypeDto the updated task type data
     * @return the updated task type
     */
    TaskTypeDto updateTaskType(Long id, TaskTypeDto taskTypeDto);

    /**
     * Delete task type
     * 
     * @param id the task type ID
     */
    void deleteTaskType(Long id);

    /**
     * Search task types by name
     * 
     * @param name the name to search for
     * @return list of task types
     */
    List<TaskTypeDto> searchTaskTypesByName(String name);

    /**
     * Check if task type exists by name
     * 
     * @param name the task type name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);
} 