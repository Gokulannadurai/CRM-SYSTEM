package com.crm.sales.service;

import com.crm.sales.dto.TaskDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Task Service Interface
 * 
 * Service interface for Task business operations.
 * Defines methods for task management and assignment.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
public interface TaskService {

    /**
     * Create a new task
     * 
     * @param taskDto the task data
     * @return the created task
     */
    TaskDto createOrUpdateTask(TaskDto taskDto, List<MultipartFile> files);

    /**
     * Get task by ID
     * 
     * @param id the task ID
     * @return the task
     */
    TaskDto getTaskById(Long id);

    /**
     * Get all tasks with pagination
     * 
     * @param pageable pagination parameters
     * @return page of tasks
     */
    Page<TaskDto> getAllTasks(Pageable pageable);

    /**
     * Delete task
     * 
     * @param id the task ID
     */
    void deleteTask(Long id);

    /**
     * Get tasks by lead ID
     * 
     * @param leadId the lead ID
     * @return list of tasks
     */
    List<TaskDto> getTasksByLeadId(Long leadId);

    /**
     * Get tasks by customer ID
     * 
     * @param customerId the customer ID
     * @return list of tasks
     */
    List<TaskDto> getTasksByCustomerId(Long customerId);

    /**
     * Get tasks by assigned user
     * 
     * @param assignedTo the assigned user ID
     * @return list of tasks
     */
    List<TaskDto> getTasksByAssignedTo(Long assignedTo);

    /**
     * Get tasks by task type
     * 
     * @param taskTypeId the task type ID
     * @return list of tasks
     */
    List<TaskDto> getTasksByTaskType(Long taskTypeId);

    /**
     * Get tasks by completion status
     * 
     * @param isCompleted the completion status
     * @return list of tasks
     */
    List<TaskDto> getTasksByCompletionStatus(Boolean isCompleted);

    /**
     * Get overdue tasks
     * 
     * @return list of overdue tasks
     */
    List<TaskDto> getOverdueTasks();

    /**
     * Get tasks due soon
     * 
     * @param hours number of hours to look ahead
     * @return list of tasks due soon
     */
    List<TaskDto> getTasksDueSoon(int hours);

    /**
     * Mark task as completed
     * 
     * @param id the task ID
     * @return the updated task
     */
    TaskDto markTaskAsCompleted(Long id);

    /**
     * Mark task as incomplete
     * 
     * @param id the task ID
     * @return the updated task
     */
    TaskDto markTaskAsIncomplete(Long id);

    /**
     * Assign task to user
     * 
     * @param id the task ID
     * @param assignedTo the user ID to assign to
     * @return the updated task
     */
    TaskDto assignTask(Long id, Long assignedTo);

    /**
     * Get task statistics
     * 
     * @return task statistics
     */
    Object getTaskStatistics();
} 