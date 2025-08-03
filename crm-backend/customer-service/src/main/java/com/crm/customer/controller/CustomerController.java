package com.crm.customer.controller;

import com.crm.customer.dto.CustomerDto;
import com.crm.customer.mapper.CustomerMapper;
import com.crm.customer.service.CustomerService;
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
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for Customer Management
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    /**
     * Create a new customer
     * 
     * @param customerDto the customer data
     * @param authentication the authentication object
     * @return the created customer
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Create a new customer", description = "Creates a new customer with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully",
                    content = @Content(schema = @Schema(implementation = CustomerDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Customer with email already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomerDto> createCustomer(
            @Valid @RequestBody CustomerDto customerDto,
            Authentication authentication) {
        
        log.info("Creating new customer: {}", customerDto.getEmail());
        
        String createdBy = authentication != null ? authentication.getName() : "system";
        CustomerDto createdCustomer = customerMapper.toDto(
                customerService.createCustomer(customerMapper.toEntity(customerDto), createdBy));
        
        log.info("Customer created successfully with ID: {}", createdCustomer.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdCustomer);
    }

    /**
     * Get customer by ID
     * 
     * @param id the customer ID
     * @return the customer if found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Get customer by ID", description = "Retrieves a customer by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(schema = @Schema(implementation = CustomerDto.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomerDto> getCustomerById(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable @Min(1) Long id) {
        
        log.debug("Fetching customer by ID: {}", id);
        
        Optional<CustomerDto> customer = customerService.getCustomerById(id)
                .map(customerMapper::toDto);
        
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get customer by email
     * 
     * @param email the customer email
     * @return the customer if found
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Get customer by email", description = "Retrieves a customer by their email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(schema = @Schema(implementation = CustomerDto.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomerDto> getCustomerByEmail(
            @Parameter(description = "Customer email", required = true)
            @PathVariable String email) {
        
        log.debug("Fetching customer by email: {}", email);
        
        Optional<CustomerDto> customer = customerService.getCustomerByEmail(email)
                .map(customerMapper::toDto);
        
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all customers with pagination
     * 
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the field to sort by
     * @param sortDir the sort direction
     * @return page of customers
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Get all customers", description = "Retrieves all customers with pagination and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<CustomerDto>> getAllCustomers(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction")
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.debug("Fetching customers with pagination: page={}, size={}, sortBy={}, sortDir={}", 
                page, size, sortBy, sortDir);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CustomerDto> customers = customerService.getAllCustomers(pageable)
                .map(customerMapper::toDto);
        
        return ResponseEntity.ok(customers);
    }

    /**
     * Search customers
     * 
     * @param searchTerm the search term
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of matching customers
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Search customers", description = "Searches customers by name, email, or company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<CustomerDto>> searchCustomers(
            @Parameter(description = "Search term", required = true)
            @RequestParam String searchTerm,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        log.debug("Searching customers with term: {}", searchTerm);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerDto> customers = customerService.searchCustomers(searchTerm, pageable)
                .map(customerMapper::toDto);
        
        return ResponseEntity.ok(customers);
    }

    /**
     * Get customers by active status
     * 
     * @param isActive the active status
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of customers with the specified status
     */
    @GetMapping("/status/{isActive}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Get customers by active status", description = "Retrieves customers filtered by active status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<CustomerDto>> getCustomersByActiveStatus(
            @Parameter(description = "Active status", required = true)
            @PathVariable Boolean isActive,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        log.debug("Fetching customers by active status: {}", isActive);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerDto> customers = customerService.getCustomersByActiveStatus(isActive, pageable)
                .map(customerMapper::toDto);
        
        return ResponseEntity.ok(customers);
    }

    /**
     * Get customers by company
     * 
     * @param company the company name
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of customers from the specified company
     */
    @GetMapping("/company/{company}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Get customers by company", description = "Retrieves customers from a specific company")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<CustomerDto>> getCustomersByCompany(
            @Parameter(description = "Company name", required = true)
            @PathVariable String company,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        log.debug("Fetching customers by company: {}", company);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerDto> customers = customerService.getCustomersByCompany(company, pageable)
                .map(customerMapper::toDto);
        
        return ResponseEntity.ok(customers);
    }

    /**
     * Get customers by date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of customers created in the specified date range
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Get customers by date range", description = "Retrieves customers created within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<CustomerDto>> getCustomersByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") @Min(1) int size) {
        
        log.debug("Fetching customers by date range: {} to {}", startDate, endDate);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerDto> customers = customerService.getCustomersByDateRange(startDate, endDate, pageable)
                .map(customerMapper::toDto);
        
        return ResponseEntity.ok(customers);
    }

    /**
     * Update customer
     * 
     * @param id the customer ID
     * @param customerDto the updated customer data
     * @param authentication the authentication object
     * @return the updated customer
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Update customer", description = "Updates an existing customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully",
                    content = @Content(schema = @Schema(implementation = CustomerDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "Customer with email already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomerDto> updateCustomer(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody CustomerDto customerDto,
            Authentication authentication) {
        
        log.info("Updating customer with ID: {}", id);
        
        String updatedBy = authentication != null ? authentication.getName() : "system";
        CustomerDto updatedCustomer = customerMapper.toDto(
                customerService.updateCustomer(id, customerMapper.toEntity(customerDto), updatedBy));
        
        log.info("Customer updated successfully with ID: {}", updatedCustomer.getId());
        
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * Change customer active status
     * 
     * @param id the customer ID
     * @param isActive the new active status
     * @param authentication the authentication object
     * @return the updated customer
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Change customer active status", description = "Changes the active status of a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer status updated successfully",
                    content = @Content(schema = @Schema(implementation = CustomerDto.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomerDto> changeCustomerActiveStatus(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable @Min(1) Long id,
            @Parameter(description = "New active status", required = true)
            @RequestParam Boolean isActive,
            Authentication authentication) {
        
        log.info("Changing active status for customer {} to {}", id, isActive);
        
        String updatedBy = authentication != null ? authentication.getName() : "system";
        CustomerDto updatedCustomer = customerMapper.toDto(
                customerService.changeCustomerActiveStatus(id, isActive, updatedBy));
        
        log.info("Customer active status changed successfully for ID: {}", updatedCustomer.getId());
        
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * Delete customer
     * 
     * @param id the customer ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete customer", description = "Deletes a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable @Min(1) Long id) {
        
        log.info("Deleting customer with ID: {}", id);
        
        customerService.deleteCustomer(id);
        
        log.info("Customer deleted successfully with ID: {}", id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Get customer statistics
     * 
     * @return customer statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Get customer statistics", description = "Retrieves customer statistics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomerStats> getCustomerStats() {
        
        log.debug("Fetching customer statistics");
        
        CustomerStats stats = CustomerStats.builder()
                .totalCustomers(customerService.countAllCustomers())
                .activeCustomers(customerService.countCustomersByActiveStatus(true))
                .inactiveCustomers(customerService.countCustomersByActiveStatus(false))
                .build();
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Customer statistics DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class CustomerStats {
        private long totalCustomers;
        private long activeCustomers;
        private long inactiveCustomers;
    }

    /**
     * Get all customers for dropdown (id and name only)
     * 
     * @return list of customers with id and name
     */
    @GetMapping("/dropdown")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_REPRESENTATIVE')")
    @Operation(summary = "Get customers for dropdown", description = "Retrieves all active customers for dropdown selection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> getAllCustomersForDropdown() {
        log.info("Getting all customers for dropdown");
        
        try {
            List<Map<String, Object>> customers = customerService.getAllActiveCustomers().stream()
                .map(customer -> {
                    Map<String, Object> customerMap = new HashMap<>();
                    customerMap.put("id", customer.getId());
                    customerMap.put("name", customer.getName());
                    return customerMap;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", customers);
            response.put("message", "Customers retrieved successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting customers for dropdown", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to retrieve customers");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
} 