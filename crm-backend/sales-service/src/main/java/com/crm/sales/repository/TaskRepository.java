package com.crm.sales.repository;

import com.crm.sales.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Task Repository
 * 
 * Repository interface for Task entity operations.
 * Provides custom queries for task management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find tasks by lead ID
     * 
     * @param leadId the lead ID
     * @return list of tasks
     */
    List<Task> findByLeadId(Long leadId);

    /**
     * Find tasks by customer ID
     * 
     * @param customerId the customer ID
     * @return list of tasks
     */
    List<Task> findByCustomerId(Long customerId);

    /**
     * Find tasks by assigned user
     * 
     * @param assignedTo the assigned user ID
     * @return list of tasks
     */
    List<Task> findByAssignedTo(Long assignedTo);

    /**
     * Find tasks by task type
     * 
     * @param taskTypeId the task type ID
     * @return list of tasks
     */
    List<Task> findByTaskTypeId(Long taskTypeId);

    /**
     * Find tasks by completion status
     * 
     * @param isCompleted the completion status
     * @return list of tasks
     */
    List<Task> findByIsCompleted(Boolean isCompleted);

    /**
     * Find overdue tasks
     * 
     * @return list of overdue tasks
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.isCompleted = false")
    List<Task> findOverdueTasks(@Param("now") Date now);

    /**
     * Find tasks due soon (within specified hours)
     * 
     * @param now current time
     * @param hoursFromNow number of hours from now
     * @return list of tasks due soon
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :now AND :hoursFromNow AND t.isCompleted = false")
    List<Task> findTasksDueSoon(@Param("now") Date now,
                                @Param("hoursFromNow") Date hoursFromNow);

    /**
     * Find tasks by assigned user with pagination
     * 
     * @param assignedTo the assigned user ID
     * @param pageable pagination parameters
     * @return page of tasks
     */
    Page<Task> findByAssignedTo(Long assignedTo, Pageable pageable);

    /**
     * Find tasks by completion status with pagination
     * 
     * @param isCompleted the completion status
     * @param pageable pagination parameters
     * @return page of tasks
     */
    Page<Task> findByIsCompleted(Boolean isCompleted, Pageable pageable);

    /**
     * Find tasks by task type with pagination
     * 
     * @param taskTypeId the task type ID
     * @param pageable pagination parameters
     * @return page of tasks
     */
    Page<Task> findByTaskTypeId(Long taskTypeId, Pageable pageable);

    /**
     * Search tasks by customer ID and completion status
     * 
     * @param customerId the customer ID
     * @param isCompleted the completion status
     * @return list of tasks
     */
    List<Task> findByCustomerIdAndIsCompleted(Long customerId, Boolean isCompleted);

    /**
     * Search tasks by lead ID and completion status
     * 
     * @param leadId the lead ID
     * @param isCompleted the completion status
     * @return list of tasks
     */
    List<Task> findByLeadIdAndIsCompleted(Long leadId, Boolean isCompleted);

    /**
     * Count tasks by completion status
     * 
     * @param isCompleted the completion status
     * @return count of tasks
     */
    long countByIsCompleted(Boolean isCompleted);

    /**
     * Count tasks by assigned user
     * 
     * @param assignedTo the assigned user ID
     * @return count of tasks
     */
    long countByAssignedTo(Long assignedTo);

    /**
     * Count tasks by task type
     * 
     * @param taskTypeId the task type ID
     * @return count of tasks
     */
    long countByTaskTypeId(Long taskTypeId);

    /**
     * Count overdue tasks
     * 
     * @return count of overdue tasks
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.dueDate < :now AND t.isCompleted = false")
    long countOverdueTasks(@Param("now") Date date);
} 