package com.crm.sales.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Lead Entity
 * 
 * Represents a sales lead in the CRM system.
 * A lead is a potential customer who has shown interest in the company's
 * products or services and is being tracked through the sales pipeline.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lead", indexes = {
    @Index(name = "idx_lead_customer_id", columnList = "customer_id"),
    @Index(name = "idx_lead_assigned_to", columnList = "assigned_to"),
    @Index(name = "idx_lead_status", columnList = "status"),
    @Index(name = "idx_lead_current_stage_id", columnList = "current_stage_id"),
    @Index(name = "idx_lead_expected_close_date", columnList = "expected_close_date")
})
public class Lead extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @NotNull(message = "Customer ID is required")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "assigned_to")
    private Long assignedTo;

    @Column(name = "status", nullable = false)
    private String status = "Lead"; // for quick filter

    @Column(name = "current_stage_id")
    private Long currentStageId;

    @Temporal(TemporalType.DATE)
    @Column(name = "expected_close_date")
    private Date expectedCloseDate;

    @Positive(message = "Value must be positive")
    @Column(name = "value", precision = 12, scale = 2)
    private BigDecimal value;

    @Column(name = "additional_notes", nullable = false)
    private String additionalNotes;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "user_name")
    private String userName;

    @Type(value = JsonBinaryType.class)
    @Column(name = "attachments", columnDefinition = "jsonb")
    private List<Attachment> attachments;

    /**
     * Default constructor
     */
    public Lead() {
    }

    /**
     * Constructor with basic lead information
     * 
     * @param customerId the customer ID
     * @param assignedTo the assigned user ID
     * @param status the lead status
     * @param currentStageId the current stage ID
     * @param expectedCloseDate the expected close date
     * @param value the lead value
     */
    public Lead(Long customerId, Long assignedTo, String status, 
                Long currentStageId, Date expectedCloseDate, BigDecimal value) {
        this.customerId = customerId;
        this.assignedTo = assignedTo;
        this.status = status;
        this.currentStageId = currentStageId;
        this.expectedCloseDate = expectedCloseDate;
        this.value = value;
    }
} 