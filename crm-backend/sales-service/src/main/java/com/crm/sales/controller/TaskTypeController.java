package com.crm.sales.controller;

import com.crm.sales.dto.TaskTypeDto;
import com.crm.sales.service.TaskTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Task Type Controller
 * 
 * REST controller for TaskType management operations.
 * Provides endpoints for CRUD operations and task type management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/task-types")
@RequiredArgsConstructor
public class TaskTypeController {

    private final TaskTypeService taskTypeService;

    /**
     * Create a new task type
     * 
     * @param taskTypeDto the task type data
     * @return the created task type
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<TaskTypeDto> createTaskType(@Valid @RequestBody TaskTypeDto taskTypeDto) {
        log.info("Creating new task type: {}", taskTypeDto.getName());
        TaskTypeDto createdTaskType = taskTypeService.createTaskType(taskTypeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskType);
    }

    /**
     * Get task type by ID
     * 
     * @param id the task type ID
     * @return the task type
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<TaskTypeDto> getTaskTypeById(@PathVariable Long id) {
        log.info("Getting task type by ID: {}", id);
        TaskTypeDto taskType = taskTypeService.getTaskTypeById(id);
        return ResponseEntity.ok(taskType);
    }

    /**
     * Get task type by name
     * 
     * @param name the task type name
     * @return the task type
     */
    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<TaskTypeDto> getTaskTypeByName(@PathVariable String name) {
        log.info("Getting task type by name: {}", name);
        TaskTypeDto taskType = taskTypeService.getTaskTypeByName(name);
        return ResponseEntity.ok(taskType);
    }

    /**
     * Get all task types
     * 
     * @return list of task types
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<TaskTypeDto>> getAllTaskTypes() {
        log.info("Getting all task types");
        List<TaskTypeDto> taskTypes = taskTypeService.getAllTaskTypes();
        return ResponseEntity.ok(taskTypes);
    }

    /**
     * Get all task types with pagination
     * 
     * @param pageable pagination parameters
     * @return page of task types
     */
    @GetMapping("/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<Page<TaskTypeDto>> getAllTaskTypesPaginated(Pageable pageable) {
        log.info("Getting all task types with pagination: {}", pageable);
        Page<TaskTypeDto> taskTypes = taskTypeService.getAllTaskTypes(pageable);
        return ResponseEntity.ok(taskTypes);
    }

    /**
     * Update task type
     * 
     * @param id the task type ID
     * @param taskTypeDto the updated task type data
     * @return the updated task type
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<TaskTypeDto> updateTaskType(@PathVariable Long id, @Valid @RequestBody TaskTypeDto taskTypeDto) {
        log.info("Updating task type with ID: {}", id);
        TaskTypeDto updatedTaskType = taskTypeService.updateTaskType(id, taskTypeDto);
        return ResponseEntity.ok(updatedTaskType);
    }

    /**
     * Delete task type
     * 
     * @param id the task type ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<Void> deleteTaskType(@PathVariable Long id) {
        log.info("Deleting task type with ID: {}", id);
        taskTypeService.deleteTaskType(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search task types by name
     * 
     * @param name the name to search for
     * @return list of task types
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<TaskTypeDto>> searchTaskTypesByName(@RequestParam String name) {
        log.info("Searching task types by name: {}", name);
        List<TaskTypeDto> taskTypes = taskTypeService.searchTaskTypesByName(name);
        return ResponseEntity.ok(taskTypes);
    }

    /**
     * Check if task type exists by name
     * 
     * @param name the task type name
     * @return true if exists, false otherwise
     */
    @GetMapping("/exists/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        log.info("Checking if task type exists by name: {}", name);
        boolean exists = taskTypeService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
} 