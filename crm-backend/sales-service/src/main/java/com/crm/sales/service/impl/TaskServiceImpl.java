package com.crm.sales.service.impl;

import com.crm.sales.common.AwsFileService;
import com.crm.sales.dto.TaskDto;
import com.crm.sales.entity.Attachment;
import com.crm.sales.entity.Task;
import com.crm.sales.entity.Lead;
import com.crm.sales.mapper.AttachmentMapper;
import com.crm.sales.mapper.TaskMapper;
import com.crm.sales.repository.TaskRepository;
import com.crm.sales.repository.LeadRepository;
import com.crm.sales.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Task Service Implementation
 * 
 * Implementation of TaskService interface with business logic for task management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final LeadRepository leadRepository;
    private final TaskMapper taskMapper;
    private final AttachmentMapper attachmentMapper;
    private final AwsFileService awsFileService;


    @Override
    public TaskDto createOrUpdateTask(TaskDto taskDto, List<MultipartFile> files) {
        log.info("Creating/updating task - ID: {}, Title: {}, Description: {}, CustomerId: {}", 
                taskDto.getId(), taskDto.getTitle(), taskDto.getDescription(), taskDto.getCustomerId());
        List<Attachment> attachments = new ArrayList<>();
        int count = 1;
        Task task = null;
        if (Objects.nonNull(taskDto.getId())) {
            Optional<Task> taskOptional
                    = taskRepository.findById(taskDto.getId());
            if (taskOptional.isPresent()) {
                task = taskOptional.get();
                log.info("Found existing task for update: {}", task.getId());
                // status = CommunityPostEvent.COMMUNITY_POST_UPDATED.getEventMessage();
            } else {
                log.error("Task not found for update with ID: {}", taskDto.getId());
                throw new RuntimeException("Task not found with ID: " + taskDto.getId());
            }
        } else {
            task = taskMapper.toEntity(taskDto);
            task.setIsCompleted(false);
            task.setIsStarted(false);
            log.info("Creating new task entity");
        }
        if (files != null && !files.isEmpty()) {
            for (MultipartFile imageFile : files) {
                String imageUrl = awsFileService.uploadFile(imageFile);
                attachments.add(attachmentMapper.converToAttachment(imageUrl, count,
                        imageFile.getOriginalFilename()));
                count++;
            }
        }
        task.setAttachments(attachments);
        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", savedTask.getId());
        taskDto = taskMapper.toDto(savedTask);
        return awsFileService.setAttachmentsToTask(task, taskDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long id) {
        log.info("Getting task by ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
        
        // Fetch related lead if task has a leadId
        Lead relatedLead = null;
        if (task.getLeadId() != null) {
            relatedLead = leadRepository.findById(task.getLeadId()).orElse(null);
            log.info("Found related lead for task ID {}: {}", id, relatedLead != null ? relatedLead.getId() : "null");
        }
        
        // Use mapper with lead
        TaskDto taskDto = taskMapper.toDtoWithLead(task, relatedLead);
        return awsFileService.setAttachmentsToTask(task, taskDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDto> getAllTasks(Pageable pageable) {
        log.info("Getting all tasks with pagination: {}", pageable);
        
        Page<Task> tasks = taskRepository.findAll(pageable);
        return tasks.map(taskMapper::toDto);
    }

    @Override
    public void deleteTask(Long id) {
        log.info("Deleting task with ID: {}", id);
        
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with ID: " + id);
        }
        
        taskRepository.deleteById(id);
        log.info("Task deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByLeadId(Long leadId) {
        log.info("Getting tasks for lead: {}", leadId);
        
        List<Task> tasks = taskRepository.findByLeadId(leadId);
        return taskMapper.toDtoList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByCustomerId(Long customerId) {
        log.info("Getting tasks for customer: {}", customerId);
        
        List<Task> tasks = taskRepository.findByCustomerId(customerId);
        return taskMapper.toDtoList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByAssignedTo(Long assignedTo) {
        log.info("Getting tasks assigned to user: {}", assignedTo);
        
        List<Task> tasks = taskRepository.findByAssignedTo(assignedTo);
        return taskMapper.toDtoList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByTaskType(Long taskTypeId) {
        log.info("Getting tasks by task type: {}", taskTypeId);
        
        List<Task> tasks = taskRepository.findByTaskTypeId(taskTypeId);
        return taskMapper.toDtoList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByCompletionStatus(Boolean isCompleted) {
        log.info("Getting tasks by completion status: {}", isCompleted);
        
        List<Task> tasks = taskRepository.findByIsCompleted(isCompleted);
        return taskMapper.toDtoList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getOverdueTasks() {
        log.info("Getting overdue tasks");
        
        List<Task> tasks = taskRepository.findOverdueTasks(new Date());
        return taskMapper.toDtoList(tasks);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksDueSoon(int hours) {
        log.info("Getting tasks due soon within {} hours", hours);

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR, hours);
        Date hoursFromNow = calendar.getTime();
        List<Task> tasks = taskRepository.findTasksDueSoon(now, hoursFromNow);
        return taskMapper.toDtoList(tasks);
    }

    @Override
    public TaskDto markTaskAsCompleted(Long id) {
        log.info("Marking task {} as completed", id);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
        
        task.markAsCompleted();
        Task updatedTask = taskRepository.save(task);
        
        log.info("Task {} marked as completed", id);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    public TaskDto markTaskAsIncomplete(Long id) {
        log.info("Marking task {} as incomplete", id);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
        
        task.markAsIncomplete();
        Task updatedTask = taskRepository.save(task);
        
        log.info("Task {} marked as incomplete", id);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    public TaskDto assignTask(Long id, Long assignedTo) {
        log.info("Assigning task {} to user {}", id, assignedTo);
        
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));
        
        task.setAssignedTo(assignedTo);
        Task updatedTask = taskRepository.save(task);
        
        log.info("Task {} assigned to user {}", id, assignedTo);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getTaskStatistics() {
        log.info("Getting task statistics");
        
        Map<String, Object> statistics = new HashMap<>();
        
        // Total tasks
        long totalTasks = taskRepository.count();
        statistics.put("totalTasks", totalTasks);
        
        // Completed tasks
        long completedTasks = taskRepository.countByIsCompleted(true);
        statistics.put("completedTasks", completedTasks);
        
        // Pending tasks
        long pendingTasks = taskRepository.countByIsCompleted(false);
        statistics.put("pendingTasks", pendingTasks);
        
        // Overdue tasks
        long overdueTasks = taskRepository.countOverdueTasks(new Date());
        statistics.put("overdueTasks", overdueTasks);
        
        // Completion rate
        double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        statistics.put("completionRate", Math.round(completionRate * 100.0) / 100.0);

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR, 24);
        Date next24Hours = calendar.getTime();
        List<Task> tasksDueSoon = taskRepository.findTasksDueSoon(now, next24Hours);
        statistics.put("tasksDueSoon", tasksDueSoon.size());
        
        log.info("Task statistics generated: {}", statistics);
        return statistics;
    }
} 