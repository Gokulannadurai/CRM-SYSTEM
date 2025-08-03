package com.crm.sales.service.impl;

import com.crm.sales.dto.TaskTypeDto;
import com.crm.sales.entity.TaskType;
import com.crm.sales.mapper.TaskTypeMapper;
import com.crm.sales.repository.TaskTypeRepository;
import com.crm.sales.service.TaskTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Task Type Service Implementation
 * 
 * Implementation of TaskTypeService interface with business logic for task type management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskTypeServiceImpl implements TaskTypeService {

    private final TaskTypeRepository taskTypeRepository;
    private final TaskTypeMapper taskTypeMapper;

    @Override
    public TaskTypeDto createTaskType(TaskTypeDto taskTypeDto) {
        log.info("Creating new task type: {}", taskTypeDto.getName());
        
        // Check if task type already exists
        if (taskTypeRepository.existsByNameIgnoreCase(taskTypeDto.getName())) {
            throw new RuntimeException("Task type with name '" + taskTypeDto.getName() + "' already exists");
        }
        
        TaskType taskType = taskTypeMapper.toEntity(taskTypeDto);
        TaskType savedTaskType = taskTypeRepository.save(taskType);
        
        log.info("Task type created successfully with ID: {}", savedTaskType.getId());
        return taskTypeMapper.toDto(savedTaskType);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskTypeDto getTaskTypeById(Long id) {
        log.info("Getting task type by ID: {}", id);
        
        TaskType taskType = taskTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task type not found with ID: " + id));
        
        return taskTypeMapper.toDto(taskType);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskTypeDto getTaskTypeByName(String name) {
        log.info("Getting task type by name: {}", name);
        
        TaskType taskType = taskTypeRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Task type not found with name: " + name));
        
        return taskTypeMapper.toDto(taskType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskTypeDto> getAllTaskTypes() {
        log.info("Getting all task types");
        
        List<TaskType> taskTypes = taskTypeRepository.findAllByOrderByNameAsc();
        return taskTypeMapper.toDtoList(taskTypes);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskTypeDto> getAllTaskTypes(Pageable pageable) {
        log.info("Getting all task types with pagination: {}", pageable);
        
        Page<TaskType> taskTypes = taskTypeRepository.findAllByOrderByNameAsc(pageable);
        return taskTypes.map(taskTypeMapper::toDto);
    }

    @Override
    public TaskTypeDto updateTaskType(Long id, TaskTypeDto taskTypeDto) {
        log.info("Updating task type with ID: {}", id);
        
        TaskType existingTaskType = taskTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task type not found with ID: " + id));
        
        // Check if the new name conflicts with another task type
        if (!existingTaskType.getName().equalsIgnoreCase(taskTypeDto.getName()) &&
            taskTypeRepository.existsByNameIgnoreCase(taskTypeDto.getName())) {
            throw new RuntimeException("Task type with name '" + taskTypeDto.getName() + "' already exists");
        }
        
        // Update the name
        existingTaskType.setName(taskTypeDto.getName());
        
        TaskType updatedTaskType = taskTypeRepository.save(existingTaskType);
        log.info("Task type updated successfully with ID: {}", updatedTaskType.getId());
        
        return taskTypeMapper.toDto(updatedTaskType);
    }

    @Override
    public void deleteTaskType(Long id) {
        log.info("Deleting task type with ID: {}", id);
        
        TaskType taskType = taskTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task type not found with ID: " + id));
        
        // Check if task type is being used by any tasks
        // This would require a join query or additional repository method
        // For now, we'll allow deletion and handle foreign key constraints at database level
        
        taskTypeRepository.deleteById(id);
        log.info("Task type deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskTypeDto> searchTaskTypesByName(String name) {
        log.info("Searching task types by name: {}", name);
        
        List<TaskType> taskTypes = taskTypeRepository.findByNameContainingIgnoreCase(name);
        return taskTypeMapper.toDtoList(taskTypes);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        log.info("Checking if task type exists by name: {}", name);
        
        return taskTypeRepository.existsByNameIgnoreCase(name);
    }
} 