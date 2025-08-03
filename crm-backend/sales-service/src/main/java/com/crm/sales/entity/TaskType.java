package com.crm.sales.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Task Type Entity
 * 
 * Represents different types of tasks that can be created in the CRM system.
 * Examples include: Follow-up, Call, Demo, Meeting, Email, etc.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "task_type", indexes = {
    @Index(name = "idx_task_type_name", columnList = "name")
})
public class TaskType extends BaseEntity {

    @NotBlank(message = "Task type name is required")
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    /**
     * Default constructor
     */
    public TaskType() {
    }

    /**
     * Constructor with task type name
     * 
     * @param name the task type name
     */
    public TaskType(String name) {
        this.name = name;
    }

    /**
     * Check if this is a communication task type
     * 
     * @return true if communication task, false otherwise
     */
    public boolean isCommunicationTask() {
        return name != null && (name.equalsIgnoreCase("Call") || 
                               name.equalsIgnoreCase("Email") || 
                               name.equalsIgnoreCase("Meeting"));
    }

    /**
     * Check if this is a follow-up task type
     * 
     * @return true if follow-up task, false otherwise
     */
    public boolean isFollowUpTask() {
        return name != null && name.equalsIgnoreCase("Follow-up");
    }

    /**
     * Check if this is a demo task type
     * 
     * @return true if demo task, false otherwise
     */
    public boolean isDemoTask() {
        return name != null && name.equalsIgnoreCase("Demo");
    }
} 