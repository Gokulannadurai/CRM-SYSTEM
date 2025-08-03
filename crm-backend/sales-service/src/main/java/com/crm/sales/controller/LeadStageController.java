package com.crm.sales.controller;

import com.crm.sales.dto.LeadStageDto;
import com.crm.sales.service.LeadStageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Lead Stage Controller
 * 
 * REST controller for LeadStage management operations.
 * Provides endpoints for CRUD operations and lead stage management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/lead-stages")
@RequiredArgsConstructor
public class LeadStageController {

    private final LeadStageService leadStageService;

    /**
     * Create a new lead stage
     * 
     * @param leadStageDto the lead stage data
     * @return the created lead stage
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<LeadStageDto> createLeadStage(@Valid @RequestBody LeadStageDto leadStageDto) {
        log.info("Creating new lead stage: {}", leadStageDto.getName());
        LeadStageDto createdLeadStage = leadStageService.createLeadStage(leadStageDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLeadStage);
    }

    /**
     * Get lead stage by ID
     * 
     * @param id the lead stage ID
     * @return the lead stage
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<LeadStageDto> getLeadStageById(@PathVariable Long id) {
        log.info("Getting lead stage by ID: {}", id);
        LeadStageDto leadStage = leadStageService.getLeadStageById(id);
        return ResponseEntity.ok(leadStage);
    }

    /**
     * Get lead stage by name
     * 
     * @param name the lead stage name
     * @return the lead stage
     */
    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<LeadStageDto> getLeadStageByName(@PathVariable String name) {
        log.info("Getting lead stage by name: {}", name);
        LeadStageDto leadStage = leadStageService.getLeadStageByName(name);
        return ResponseEntity.ok(leadStage);
    }

    /**
     * Get lead stage by position
     * 
     * @param position the position
     * @return the lead stage
     */
    @GetMapping("/position/{position}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<LeadStageDto> getLeadStageByPosition(@PathVariable Integer position) {
        log.info("Getting lead stage by position: {}", position);
        LeadStageDto leadStage = leadStageService.getLeadStageByPosition(position);
        return ResponseEntity.ok(leadStage);
    }

    /**
     * Get all lead stages
     * 
     * @return list of lead stages
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<List<LeadStageDto>> getAllLeadStages() {
        log.info("Getting all lead stages");
        List<LeadStageDto> leadStages = leadStageService.getAllLeadStages();
        return ResponseEntity.ok(leadStages);
    }

    /**
     * Get all lead stages with pagination
     * 
     * @param pageable pagination parameters
     * @return page of lead stages
     */
    @GetMapping("/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<Page<LeadStageDto>> getAllLeadStagesPaginated(Pageable pageable) {
        log.info("Getting all lead stages with pagination: {}", pageable);
        Page<LeadStageDto> leadStages = leadStageService.getAllLeadStages(pageable);
        return ResponseEntity.ok(leadStages);
    }

    /**
     * Update lead stage
     * 
     * @param id the lead stage ID
     * @param leadStageDto the updated lead stage data
     * @return the updated lead stage
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<LeadStageDto> updateLeadStage(@PathVariable Long id, @Valid @RequestBody LeadStageDto leadStageDto) {
        log.info("Updating lead stage with ID: {}", id);
        LeadStageDto updatedLeadStage = leadStageService.updateLeadStage(id, leadStageDto);
        return ResponseEntity.ok(updatedLeadStage);
    }

    /**
     * Delete lead stage
     * 
     * @param id the lead stage ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public ResponseEntity<Void> deleteLeadStage(@PathVariable Long id) {
        log.info("Deleting lead stage with ID: {}", id);
        leadStageService.deleteLeadStage(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get the first stage
     * 
     * @return the first lead stage
     */
    @GetMapping("/first")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<LeadStageDto> getFirstStage() {
        log.info("Getting first lead stage");
        LeadStageDto firstStage = leadStageService.getFirstStage();
        return ResponseEntity.ok(firstStage);
    }

    /**
     * Get the last stage
     * 
     * @return the last lead stage
     */
    @GetMapping("/last")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<LeadStageDto> getLastStage() {
        log.info("Getting last lead stage");
        LeadStageDto lastStage = leadStageService.getLastStage();
        return ResponseEntity.ok(lastStage);
    }

    /**
     * Get next stage by current position
     * 
     * @param currentPosition the current position
     * @return the next lead stage
     */
    @GetMapping("/next/{currentPosition}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<LeadStageDto> getNextStage(@PathVariable Integer currentPosition) {
        log.info("Getting next lead stage from position: {}", currentPosition);
        LeadStageDto nextStage = leadStageService.getNextStage(currentPosition);
        return ResponseEntity.ok(nextStage);
    }

    /**
     * Get previous stage by current position
     * 
     * @param currentPosition the current position
     * @return the previous lead stage
     */
    @GetMapping("/previous/{currentPosition}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<LeadStageDto> getPreviousStage(@PathVariable Integer currentPosition) {
        log.info("Getting previous lead stage from position: {}", currentPosition);
        LeadStageDto previousStage = leadStageService.getPreviousStage(currentPosition);
        return ResponseEntity.ok(previousStage);
    }

    /**
     * Check if lead stage exists by name
     * 
     * @param name the lead stage name
     * @return true if exists, false otherwise
     */
    @GetMapping("/exists/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        log.info("Checking if lead stage exists by name: {}", name);
        boolean exists = leadStageService.existsByName(name);
        return ResponseEntity.ok(exists);
    }

    /**
     * Check if lead stage exists by position
     * 
     * @param position the position
     * @return true if exists, false otherwise
     */
    @GetMapping("/exists/position/{position}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    public ResponseEntity<Boolean> existsByPosition(@PathVariable Integer position) {
        log.info("Checking if lead stage exists by position: {}", position);
        boolean exists = leadStageService.existsByPosition(position);
        return ResponseEntity.ok(exists);
    }
} 