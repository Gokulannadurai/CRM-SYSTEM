package com.crm.sales.controller;

import com.crm.sales.dto.LeadDto;
import com.crm.sales.dto.TaskDto;
import com.crm.sales.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ArrayList;

/**
 * Task Controller
 * 
 * REST controller for Task management operations.
 * Provides endpoints for CRUD operations and task management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Create or update a task
     * 
     * @param taskRequest the task data
     * @param files the attachment files
     * @return the created/updated task
     */
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<TaskDto> createOrUpdateTask(@RequestParam("taskRequest") String taskRequest,
                                                      @RequestParam(value = "attachments", required = false) List<MultipartFile> files) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info("Received task request: {}", taskRequest);
            TaskDto taskDto = mapper.readValue(taskRequest, TaskDto.class);
            log.info("Parsed task DTO - ID: {}, Title: {}, Description: {}, CustomerId: {}", 
                    taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getCustomerId());
            TaskDto result = taskService.createOrUpdateTask(taskDto, files!=null
                    ? files:new ArrayList<>());
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            log.error("Error creating/updating task: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get task by ID
     * 
     * @param id the task ID
     * @return the task
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        log.info("Getting task by ID: {}", id);
        TaskDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * Get all tasks with pagination
     * 
     * @param pageable pagination parameters
     * @return page of tasks
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<Page<TaskDto>> getAllTasks(Pageable pageable) {
        log.info("Getting all tasks with pagination: {}", pageable);
        Page<TaskDto> tasks = taskService.getAllTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Delete task
     * 
     * @param id the task ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("Deleting task with ID: {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get tasks by lead ID
     * 
     * @param leadId the lead ID
     * @return list of tasks
     */
    @GetMapping("/lead/{leadId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<TaskDto>> getTasksByLeadId(@PathVariable Long leadId) {
        log.info("Getting tasks for lead: {}", leadId);
        List<TaskDto> tasks = taskService.getTasksByLeadId(leadId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by customer ID
     * 
     * @param customerId the customer ID
     * @return list of tasks
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<TaskDto>> getTasksByCustomerId(@PathVariable Long customerId) {
        log.info("Getting tasks for customer: {}", customerId);
        List<TaskDto> tasks = taskService.getTasksByCustomerId(customerId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by assigned user
     * 
     * @param assignedTo the assigned user ID
     * @return list of tasks
     */
    @GetMapping("/assigned/{assignedTo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<TaskDto>> getTasksByAssignedTo(@PathVariable Long assignedTo) {
        log.info("Getting tasks assigned to user: {}", assignedTo);
        List<TaskDto> tasks = taskService.getTasksByAssignedTo(assignedTo);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by task type
     * 
     * @param taskTypeId the task type ID
     * @return list of tasks
     */
    @GetMapping("/type/{taskTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<TaskDto>> getTasksByTaskType(@PathVariable Long taskTypeId) {
        log.info("Getting tasks by task type: {}", taskTypeId);
        List<TaskDto> tasks = taskService.getTasksByTaskType(taskTypeId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by completion status
     * 
     * @param isCompleted the completion status
     * @return list of tasks
     */
    @GetMapping("/status/{isCompleted}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<TaskDto>> getTasksByCompletionStatus(@PathVariable Boolean isCompleted) {
        log.info("Getting tasks by completion status: {}", isCompleted);
        List<TaskDto> tasks = taskService.getTasksByCompletionStatus(isCompleted);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get overdue tasks
     * 
     * @return list of overdue tasks
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<TaskDto>> getOverdueTasks() {
        log.info("Getting overdue tasks");
        List<TaskDto> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks due soon
     * 
     * @param hours number of hours to look ahead
     * @return list of tasks due soon
     */
    @GetMapping("/due-soon")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<TaskDto>> getTasksDueSoon(@RequestParam(defaultValue = "24") int hours) {
        log.info("Getting tasks due soon within {} hours", hours);
        List<TaskDto> tasks = taskService.getTasksDueSoon(hours);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Mark task as completed
     * 
     * @param id the task ID
     * @return the updated task
     */
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<TaskDto> markTaskAsCompleted(@PathVariable Long id) {
        log.info("Marking task {} as completed", id);
        TaskDto updatedTask = taskService.markTaskAsCompleted(id);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Mark task as incomplete
     * 
     * @param id the task ID
     * @return the updated task
     */
    @PatchMapping("/{id}/incomplete")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<TaskDto> markTaskAsIncomplete(@PathVariable Long id) {
        log.info("Marking task {} as incomplete", id);
        TaskDto updatedTask = taskService.markTaskAsIncomplete(id);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Assign task to user
     * 
     * @param id the task ID
     * @param assignedTo the user ID to assign to
     * @return the updated task
     */
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<TaskDto> assignTask(@PathVariable Long id, @RequestParam Long assignedTo) {
        log.info("Assigning task {} to user {}", id, assignedTo);
        TaskDto updatedTask = taskService.assignTask(id, assignedTo);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Get task statistics
     * 
     * @return task statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<Object> getTaskStatistics() {
        log.info("Getting task statistics");
        Object statistics = taskService.getTaskStatistics();
        return ResponseEntity.ok(statistics);
    }
} 