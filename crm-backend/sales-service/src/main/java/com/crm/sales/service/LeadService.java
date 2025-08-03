package com.crm.sales.service;

import com.crm.sales.dto.LeadDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * Lead Service Interface
 * 
 * Service interface for Lead business operations.
 * Defines methods for lead management and pipeline operations.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
public interface LeadService {

    /**
     * Create a new lead
     * 
     * @param leadDto the lead data
     * @return the created lead
     */
    LeadDto createOrUpdateLead(LeadDto leadDto, List<MultipartFile> files);

    /**
     * Get lead by ID
     * 
     * @param id the lead ID
     * @return the lead
     */
    LeadDto getLeadById(Long id);

    /**
     * Get all leads with pagination
     * 
     * @param pageable pagination parameters
     * @return page of leads
     */
    Page<LeadDto> getAllLeads(Pageable pageable);


    /**
     * Delete lead
     * 
     * @param id the lead ID
     */
    void deleteLead(Long id);

    /**
     * Get leads by customer ID
     * 
     * @param customerId the customer ID
     * @return list of leads
     */
    List<LeadDto> getLeadsByCustomerId(Long customerId);

    /**
     * Get leads by assigned user
     * 
     * @param assignedTo the assigned user ID
     * @return list of leads
     */
    List<LeadDto> getLeadsByAssignedTo(Long assignedTo);

    /**
     * Get leads by status
     * 
     * @param status the lead status
     * @return list of leads
     */
    List<LeadDto> getLeadsByStatus(String status);

    /**
     * Get leads by current stage
     * 
     * @param currentStageId the current stage ID
     * @return list of leads
     */
    List<LeadDto> getLeadsByCurrentStage(Long currentStageId);

    /**
     * Get overdue leads
     * 
     * @return list of overdue leads
     */
    List<LeadDto> getOverdueLeads();

    /**
     * Get leads due soon
     * 
     * @param days number of days to look ahead
     * @return list of leads due soon
     */
    List<LeadDto> getLeadsDueSoon(int days);

    /**
     * Move lead to next stage
     * 
     * @param id the lead ID
     * @return the updated lead
     */
    LeadDto moveToNextStage(Long id);

    /**
     * Move lead to previous stage
     * 
     * @param id the lead ID
     * @return the updated lead
     */
    LeadDto moveToPreviousStage(Long id);

    /**
     * Assign lead to user
     * 
     * @param id the lead ID
     * @param assignedTo the user ID to assign to
     * @return the updated lead
     */
    LeadDto assignLead(Long id, Long assignedTo);

    /**
     * Get lead statistics
     * 
     * @return lead statistics
     */
    Object getLeadStatistics();

    /**
     * Get pipeline statistics
     * 
     * @return pipeline statistics
     */
    Object getPipelineStatistics();

    /**
     * Get all leads for dropdown
     * 
     * @return list of leads for dropdown
     */
    List<LeadDto> getAllLeadsForDropdown();
} 