package com.crm.customer.controller;

import com.crm.customer.dto.InteractionHistoryDto;
import com.crm.customer.mapper.InteractionHistoryMapper;
import com.crm.customer.service.InteractionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Interaction History Management
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@RestController
@RequestMapping("/api/v1/interaction-history")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Interaction History Management", description = "APIs for managing customer interaction history")
public class InteractionHistoryController {

    private final InteractionHistoryService interactionHistoryService;
    private final InteractionHistoryMapper interactionHistoryMapper;

    /**
     * Create a new interaction history record
     * 
     * @param interactionHistoryDto the interaction history data
     * @param authentication the authentication object
     * @return the created interaction history
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Create interaction history", description = "Creates a new interaction history record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Interaction history created successfully",
                    content = @Content(schema = @Schema(implementation = InteractionHistoryDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InteractionHistoryDto> createInteractionHistory(
            @Valid @RequestBody InteractionHistoryDto interactionHistoryDto,
            Authentication authentication) {
        
        log.info("Creating new interaction history for customer: {}", interactionHistoryDto.getCustomerId());
        
        // Set the user ID from authentication if not provided
        if (interactionHistoryDto.getUserId() == null && authentication != null) {
            try {
                interactionHistoryDto.setUserId(Long.parseLong(authentication.getName()));
            } catch (NumberFormatException e) {
                log.warn("Could not parse user ID from authentication: {}", authentication.getName());
            }
        }
        
        InteractionHistoryDto createdInteractionHistory = interactionHistoryMapper.toDto(
                interactionHistoryService.createInteractionHistory(interactionHistoryMapper.toEntity(interactionHistoryDto)));
        
        log.info("Interaction history created successfully with ID: {}", createdInteractionHistory.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdInteractionHistory);
    }

    /**
     * Get interaction history by ID
     * 
     * @param id the interaction history ID
     * @return the interaction history if found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Get interaction history by ID", description = "Retrieves an interaction history record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interaction history found",
                    content = @Content(schema = @Schema(implementation = InteractionHistoryDto.class))),
            @ApiResponse(responseCode = "404", description = "Interaction history not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InteractionHistoryDto> getInteractionHistoryById(
            @Parameter(description = "Interaction history ID", required = true)
            @PathVariable @Min(1) Long id) {
        
        log.debug("Fetching interaction history by ID: {}", id);
        
        Optional<InteractionHistoryDto> interactionHistory = interactionHistoryService.getInteractionHistoryById(id)
                .map(interactionHistoryMapper::toDto);
        
        return interactionHistory.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get interaction history by customer ID
     * 
     * @param customerId the customer ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of interaction history
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Get interaction history by customer ID", description = "Retrieves interaction history for a specific customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interaction history retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<InteractionHistoryDto>> getInteractionHistoryByCustomerId(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable @Min(1) Long customerId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        log.debug("Fetching interaction history by customer ID: {}", customerId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InteractionHistoryDto> interactionHistory = interactionHistoryService.getInteractionHistoryByCustomerId(customerId, pageable)
                .map(interactionHistoryMapper::toDto);
        
        return ResponseEntity.ok(interactionHistory);
    }

    /**
     * Get interaction history by user ID
     * 
     * @param userId the user ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of interaction history
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Get interaction history by user ID", description = "Retrieves interaction history created by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interaction history retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<InteractionHistoryDto>> getInteractionHistoryByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(1) Long userId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        log.debug("Fetching interaction history by user ID: {}", userId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InteractionHistoryDto> interactionHistory = interactionHistoryService.getInteractionHistoryByUserId(userId, pageable)
                .map(interactionHistoryMapper::toDto);
        
        return ResponseEntity.ok(interactionHistory);
    }

    /**
     * Get interaction history by type
     * 
     * @param interactionType the interaction type
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of interaction history
     */
    @GetMapping("/type/{interactionType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Get interaction history by type", description = "Retrieves interaction history by interaction type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interaction history retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<InteractionHistoryDto>> getInteractionHistoryByType(
            @Parameter(description = "Interaction type", required = true)
            @PathVariable String interactionType,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        log.debug("Fetching interaction history by type: {}", interactionType);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InteractionHistoryDto> interactionHistory = interactionHistoryService.getInteractionHistoryByType(interactionType, pageable)
                .map(interactionHistoryMapper::toDto);
        
        return ResponseEntity.ok(interactionHistory);
    }

    /**
     * Get interaction history by customer ID and type
     * 
     * @param customerId the customer ID
     * @param interactionType the interaction type
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of interaction history
     */
    @GetMapping("/customer/{customerId}/type/{interactionType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Get interaction history by customer ID and type", description = "Retrieves interaction history for a customer by type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interaction history retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<InteractionHistoryDto>> getInteractionHistoryByCustomerIdAndType(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable @Min(1) Long customerId,
            @Parameter(description = "Interaction type", required = true)
            @PathVariable String interactionType,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        log.debug("Fetching interaction history by customer ID: {} and type: {}", customerId, interactionType);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InteractionHistoryDto> interactionHistory = interactionHistoryService.getInteractionHistoryByCustomerIdAndType(customerId, interactionType, pageable)
                .map(interactionHistoryMapper::toDto);
        
        return ResponseEntity.ok(interactionHistory);
    }

    /**
     * Get interaction history by date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of interaction history
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Get interaction history by date range", description = "Retrieves interaction history within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interaction history retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<InteractionHistoryDto>> getInteractionHistoryByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        log.debug("Fetching interaction history by date range: {} to {}", startDate, endDate);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<InteractionHistoryDto> interactionHistory = interactionHistoryService.getInteractionHistoryByDateRange(startDate, endDate, pageable)
                .map(interactionHistoryMapper::toDto);
        
        return ResponseEntity.ok(interactionHistory);
    }

    /**
     * Get recent interaction history for a customer
     * 
     * @param customerId the customer ID
     * @param limit the limit of records
     * @return list of recent interaction history
     */
    @GetMapping("/customer/{customerId}/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Get recent interaction history", description = "Retrieves recent interaction history for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recent interaction history retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InteractionHistoryDto>> getRecentInteractionHistoryByCustomerId(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable @Min(1) Long customerId,
            @Parameter(description = "Limit of records")
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        
        log.debug("Fetching recent interaction history for customer ID: {} with limit: {}", customerId, limit);
        
        List<InteractionHistoryDto> interactionHistory = interactionHistoryService.getRecentInteractionHistoryByCustomerId(customerId, limit)
                .stream()
                .map(interactionHistoryMapper::toDto)
                .toList();
        
        return ResponseEntity.ok(interactionHistory);
    }

    /**
     * Update interaction history
     * 
     * @param id the interaction history ID
     * @param interactionHistoryDto the updated interaction history data
     * @return the updated interaction history
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Update interaction history", description = "Updates an existing interaction history record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interaction history updated successfully",
                    content = @Content(schema = @Schema(implementation = InteractionHistoryDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Interaction history not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InteractionHistoryDto> updateInteractionHistory(
            @Parameter(description = "Interaction history ID", required = true)
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody InteractionHistoryDto interactionHistoryDto) {
        
        log.info("Updating interaction history with ID: {}", id);
        
        InteractionHistoryDto updatedInteractionHistory = interactionHistoryMapper.toDto(
                interactionHistoryService.updateInteractionHistory(id, interactionHistoryMapper.toEntity(interactionHistoryDto)));
        
        log.info("Interaction history updated successfully with ID: {}", updatedInteractionHistory.getId());
        
        return ResponseEntity.ok(updatedInteractionHistory);
    }

    /**
     * Delete interaction history
     * 
     * @param id the interaction history ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete interaction history", description = "Deletes an interaction history record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Interaction history deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Interaction history not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteInteractionHistory(
            @Parameter(description = "Interaction history ID", required = true)
            @PathVariable @Min(1) Long id) {
        
        log.info("Deleting interaction history with ID: {}", id);
        
        interactionHistoryService.deleteInteractionHistory(id);
        
        log.info("Interaction history deleted successfully with ID: {}", id);
        
        return ResponseEntity.noContent().build();
    }
} 