package com.crm.sales.service;

import com.crm.sales.dto.LeadStageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Lead Stage Service Interface
 * 
 * Service interface for LeadStage business operations.
 * Defines methods for lead stage management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
public interface LeadStageService {

    /**
     * Create a new lead stage
     * 
     * @param leadStageDto the lead stage data
     * @return the created lead stage
     */
    LeadStageDto createLeadStage(LeadStageDto leadStageDto);

    /**
     * Get lead stage by ID
     * 
     * @param id the lead stage ID
     * @return the lead stage
     */
    LeadStageDto getLeadStageById(Long id);

    /**
     * Get lead stage by name
     * 
     * @param name the lead stage name
     * @return the lead stage
     */
    LeadStageDto getLeadStageByName(String name);

    /**
     * Get lead stage by position
     * 
     * @param position the position
     * @return the lead stage
     */
    LeadStageDto getLeadStageByPosition(Integer position);

    /**
     * Get all lead stages
     * 
     * @return list of lead stages
     */
    List<LeadStageDto> getAllLeadStages();

    /**
     * Get all lead stages with pagination
     * 
     * @param pageable pagination parameters
     * @return page of lead stages
     */
    Page<LeadStageDto> getAllLeadStages(Pageable pageable);

    /**
     * Update lead stage
     * 
     * @param id the lead stage ID
     * @param leadStageDto the updated lead stage data
     * @return the updated lead stage
     */
    LeadStageDto updateLeadStage(Long id, LeadStageDto leadStageDto);

    /**
     * Delete lead stage
     * 
     * @param id the lead stage ID
     */
    void deleteLeadStage(Long id);

    /**
     * Get the first stage
     * 
     * @return the first lead stage
     */
    LeadStageDto getFirstStage();

    /**
     * Get the last stage
     * 
     * @return the last lead stage
     */
    LeadStageDto getLastStage();

    /**
     * Get next stage by current position
     * 
     * @param currentPosition the current position
     * @return the next lead stage
     */
    LeadStageDto getNextStage(Integer currentPosition);

    /**
     * Get previous stage by current position
     * 
     * @param currentPosition the current position
     * @return the previous lead stage
     */
    LeadStageDto getPreviousStage(Integer currentPosition);

    /**
     * Check if lead stage exists by name
     * 
     * @param name the lead stage name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if lead stage exists by position
     * 
     * @param position the position
     * @return true if exists, false otherwise
     */
    boolean existsByPosition(Integer position);
} 