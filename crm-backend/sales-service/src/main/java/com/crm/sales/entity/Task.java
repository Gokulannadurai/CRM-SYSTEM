package com.crm.sales.entity;

import jakarta.persistence.*;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.List;

/**
 * Task Entity
 * 
 * Represents a task or activity associated with leads or customers.
 * Tasks help track follow-ups, meetings, and other sales activities.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "task", indexes = {
    @Index(name = "idx_task_lead_id", columnList = "lead_id"),
    @Index(name = "idx_task_customer_id", columnList = "customer_id"),
    @Index(name = "idx_task_assigned_to", columnList = "assigned_to"),
    @Index(name = "idx_task_task_type_id", columnList = "task_type_id"),
    @Index(name = "idx_task_due_date", columnList = "due_date"),
    @Index(name = "idx_task_is_completed", columnList = "is_completed")
})
public class Task extends BaseEntity {

    @Column(name = "lead_id")
    private Long leadId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "assigned_to")
    private Long assignedTo;

    @Column(name = "task_type_id")
    private Long taskTypeId;

    @NotBlank(message = "Task title is required")
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    @Column(name = "is_started", nullable = false)
    private Boolean isStarted = false;

    @Type(value = JsonBinaryType.class)
    @Column(name = "attachments", columnDefinition = "jsonb")
    private List<Attachment> attachments;

    /**
     * Default constructor
     */
    public Task() {
    }

    /**
     * Constructor with basic task information
     * 
     * @param leadId the lead ID
     * @param customerId the customer ID
     * @param assignedTo the assigned user ID
     * @param taskTypeId the task type ID
     * @param title the task title
     * @param description the task description
     * @param dueDate the due date
     */
    public Task(Long leadId, Long customerId, Long assignedTo, Long taskTypeId, 
                String title, String description, Date dueDate) {
        this.leadId = leadId;
        this.customerId = customerId;
        this.assignedTo = assignedTo;
        this.taskTypeId = taskTypeId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    /**
     * Check if the task is overdue
     * 
     * @return true if overdue, false otherwise
     */
    public boolean isOverdue() {
        return dueDate != null && dueDate.before(new Date()) && !isCompleted;
    }

    /**
     * Check if the task is due soon (within 24 hours)
     * 
     * @return true if due soon, false otherwise
     */
    public boolean isDueSoon() {
        if (dueDate == null || isCompleted) {
            return false;
        }
        Date now = new Date();
        Date twentyFourHoursFromNow = new Date(now.getTime() + 24 * 60 * 60 * 1000);
        return !dueDate.before(now) && !dueDate.after(twentyFourHoursFromNow);
    }

    /**
     * Mark the task as completed
     */
    public void markAsCompleted() {
        this.isCompleted = true;
    }

    /**
     * Mark the task as incomplete
     */
    public void markAsIncomplete() {
        this.isCompleted = false;
    }
} 