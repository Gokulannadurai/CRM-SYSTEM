package com.crm.sales.controller;

import com.crm.sales.dto.LeadDto;
import com.crm.sales.service.LeadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lead Controller
 * 
 * REST controller for Lead management operations.
 * Provides endpoints for CRUD operations and pipeline management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    /**
     * Create or update a lead
     * 
     * @param leadRequest the lead data
     * @param files the attachment files
     * @return the created/updated lead
     */
    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<LeadDto> createOrUpdateLead(@RequestParam("leadRequest") String leadRequest,
                                                      @RequestParam(value = "attachments", required = false) List<MultipartFile> files) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info("Received lead request: {}", leadRequest);
            LeadDto leadDto = mapper.readValue(leadRequest, LeadDto.class);
            log.info("Parsed lead DTO - ID: {}, Title: {}, CustomerId: {}, AdditionalNotes: {}", 
                    leadDto.getId(), leadDto.getTitle(), leadDto.getCustomerId(), leadDto.getAdditionalNotes());
            LeadDto result = leadService.createOrUpdateLead(leadDto, files!=null ? files:new ArrayList<>());
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            log.error("Error creating/updating lead: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get lead by ID
     * 
     * @param id the lead ID
     * @return the lead
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<LeadDto> getLeadById(@PathVariable Long id) {
        log.info("Getting lead by ID: {}", id);
        LeadDto lead = leadService.getLeadById(id);
        return ResponseEntity.ok(lead);
    }

    /**
     * Get all leads with pagination
     * 
     * @param pageable pagination parameters
     * @return page of leads
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<Page<LeadDto>> getAllLeads(Pageable pageable) {
        log.info("Getting all leads with pagination: {}", pageable);
        Page<LeadDto> leads = leadService.getAllLeads(pageable);
        return ResponseEntity.ok(leads);
    }

    /**
     * Delete lead
     * 
     * @param id the lead ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        log.info("Deleting lead with ID: {}", id);
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get leads by customer ID
     * 
     * @param customerId the customer ID
     * @return list of leads
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<LeadDto>> getLeadsByCustomerId(@PathVariable Long customerId) {
        log.info("Getting leads for customer: {}", customerId);
        List<LeadDto> leads = leadService.getLeadsByCustomerId(customerId);
        return ResponseEntity.ok(leads);
    }

    /**
     * Get leads by assigned user
     * 
     * @param assignedTo the assigned user ID
     * @return list of leads
     */
    @GetMapping("/assigned/{assignedTo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<LeadDto>> getLeadsByAssignedTo(@PathVariable Long assignedTo) {
        log.info("Getting leads assigned to user: {}", assignedTo);
        List<LeadDto> leads = leadService.getLeadsByAssignedTo(assignedTo);
        return ResponseEntity.ok(leads);
    }

    /**
     * Get leads by status
     * 
     * @param status the lead status
     * @return list of leads
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<LeadDto>> getLeadsByStatus(@PathVariable String status) {
        log.info("Getting leads by status: {}", status);
        List<LeadDto> leads = leadService.getLeadsByStatus(status);
        return ResponseEntity.ok(leads);
    }

    /**
     * Get leads by current stage
     * 
     * @param currentStageId the current stage ID
     * @return list of leads
     */
    @GetMapping("/stage/{currentStageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<LeadDto>> getLeadsByCurrentStage(@PathVariable Long currentStageId) {
        log.info("Getting leads by current stage: {}", currentStageId);
        List<LeadDto> leads = leadService.getLeadsByCurrentStage(currentStageId);
        return ResponseEntity.ok(leads);
    }

    /**
     * Get overdue leads
     * 
     * @return list of overdue leads
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<LeadDto>> getOverdueLeads() {
        log.info("Getting overdue leads");
        List<LeadDto> leads = leadService.getOverdueLeads();
        return ResponseEntity.ok(leads);
    }

    /**
     * Get leads due soon
     * 
     * @param days number of days to look ahead
     * @return list of leads due soon
     */
    @GetMapping("/due-soon")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<LeadDto>> getLeadsDueSoon(@RequestParam(defaultValue = "7") int days) {
        log.info("Getting leads due soon within {} days", days);
        List<LeadDto> leads = leadService.getLeadsDueSoon(days);
        return ResponseEntity.ok(leads);
    }

    /**
     * Move lead to next stage
     * 
     * @param id the lead ID
     * @return the updated lead
     */
    @PatchMapping("/{id}/next-stage")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<LeadDto> moveToNextStage(@PathVariable Long id) {
        log.info("Moving lead {} to next stage", id);
        LeadDto updatedLead = leadService.moveToNextStage(id);
        return ResponseEntity.ok(updatedLead);
    }

    /**
     * Move lead to previous stage
     * 
     * @param id the lead ID
     * @return the updated lead
     */
    @PatchMapping("/{id}/previous-stage")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<LeadDto> moveToPreviousStage(@PathVariable Long id) {
        log.info("Moving lead {} to previous stage", id);
        LeadDto updatedLead = leadService.moveToPreviousStage(id);
        return ResponseEntity.ok(updatedLead);
    }

    /**
     * Assign lead to user
     * 
     * @param id the lead ID
     * @param assignedTo the user ID to assign to
     * @return the updated lead
     */
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<LeadDto> assignLead(@PathVariable Long id, @RequestParam Long assignedTo) {
        log.info("Assigning lead {} to user {}", id, assignedTo);
        LeadDto updatedLead = leadService.assignLead(id, assignedTo);
        return ResponseEntity.ok(updatedLead);
    }

    /**
     * Get lead statistics
     * 
     * @return lead statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<Object> getLeadStatistics() {
        log.info("Getting lead statistics");
        Object statistics = leadService.getLeadStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get pipeline statistics
     * 
     * @return pipeline statistics
     */
    @GetMapping("/pipeline/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<Object> getPipelineStatistics() {
        log.info("Getting pipeline statistics");
        Object statistics = leadService.getPipelineStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get all leads for dropdown (id and title only)
     * 
     * @return list of leads with id and title
     */
    @GetMapping("/dropdown")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<Map<String, Object>> getAllLeadsForDropdown() {
        log.info("Getting all leads for dropdown");
        
        try {
            List<Map<String, Object>> leads = leadService.getAllLeadsForDropdown().stream()
                .map(lead -> {
                    Map<String, Object> leadMap = new HashMap<>();
                    leadMap.put("id", lead.getId());
                    leadMap.put("title", lead.getTitle());
                    return leadMap;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", leads);
            response.put("message", "Leads retrieved successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting leads for dropdown", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to retrieve leads");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
} 