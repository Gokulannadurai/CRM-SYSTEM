package com.crm.sales.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Lead Stage Entity
 * 
 * Represents a stage in the sales pipeline for leads.
 * Each lead progresses through different stages from initial contact to closure.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "lead_stage", indexes = {
    @Index(name = "idx_lead_stage_name", columnList = "name"),
    @Index(name = "idx_lead_stage_position", columnList = "position")
})
public class LeadStage extends BaseEntity {

    @NotBlank(message = "Stage name is required")
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @NotNull(message = "Position is required")
    @Positive(message = "Position must be positive")
    @Column(name = "position", nullable = false)
    private Integer position;

    /**
     * Default constructor
     */
    public LeadStage() {
    }

    /**
     * Constructor with stage information
     * 
     * @param name the stage name
     * @param position the stage position in the pipeline
     */
    public LeadStage(String name, Integer position) {
        this.name = name;
        this.position = position;
    }

    /**
     * Check if this is the first stage in the pipeline
     * 
     * @return true if first stage, false otherwise
     */
    public boolean isFirstStage() {
        return position != null && position == 1;
    }

    /**
     * Check if this is the last stage in the pipeline
     * 
     * @return true if last stage, false otherwise
     */
    public boolean isLastStage() {
        return position != null && position >= 5; // Assuming 5+ stages are closing stages
    }

    /**
     * Check if this is a closing stage
     * 
     * @return true if closing stage, false otherwise
     */
    public boolean isClosingStage() {
        return name != null && (name.contains("Closed") || name.contains("Won") || name.contains("Lost"));
    }
} 