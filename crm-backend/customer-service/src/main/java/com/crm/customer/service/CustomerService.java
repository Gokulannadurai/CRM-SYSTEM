package com.crm.customer.service;

import com.crm.customer.entity.Customer;
import com.crm.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Customer Service
 * 
 * Service layer for Customer entity business logic.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Create a new customer
     * 
     * @param customer the customer to create
     * @param createdBy the user ID who created the customer
     * @return the created customer
     */
    public Customer createCustomer(Customer customer, String createdBy) {
        log.info("Creating new customer: {}", customer.getEmail());
        
        customer.setCreatedBy(parseUserId(createdBy));
        customer.setIsActive(true);
        
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());
        
        return savedCustomer;
    }

    /**
     * Get customer by ID
     * 
     * @param id the customer ID
     * @return optional customer
     */
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(Long id) {
        log.debug("Fetching customer by ID: {}", id);
        return customerRepository.findById(id);
    }

    /**
     * Get customer by email
     * 
     * @param email the customer email
     * @return optional customer
     */
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmail(String email) {
        log.debug("Fetching customer by email: {}", email);
        return customerRepository.findByEmail(email);
    }

    /**
     * Get all customers with pagination
     * 
     * @param pageable the pageable object
     * @return page of customers
     */
    @Transactional(readOnly = true)
    public Page<Customer> getAllCustomers(Pageable pageable) {
        log.debug("Fetching all customers with pagination");
        return customerRepository.findAll(pageable);
    }

    /**
     * Search customers
     * 
     * @param searchTerm the search term
     * @param pageable the pageable object
     * @return page of matching customers
     */
    @Transactional(readOnly = true)
    public Page<Customer> searchCustomers(String searchTerm, Pageable pageable) {
        log.debug("Searching customers with term: {}", searchTerm);
        return customerRepository.searchCustomers(searchTerm, pageable);
    }

    /**
     * Get customers by active status
     * 
     * @param isActive the active status
     * @param pageable the pageable object
     * @return page of customers
     */
    @Transactional(readOnly = true)
    public Page<Customer> getCustomersByActiveStatus(Boolean isActive, Pageable pageable) {
        log.debug("Fetching customers by active status: {}", isActive);
        return customerRepository.findByIsActive(isActive, pageable);
    }

    /**
     * Get customers by company
     * 
     * @param company the company name
     * @param pageable the pageable object
     * @return page of customers
     */
    @Transactional(readOnly = true)
    public Page<Customer> getCustomersByCompany(String company, Pageable pageable) {
        log.debug("Fetching customers by company: {}", company);
        return customerRepository.findByCompany(company, pageable);
    }

    /**
     * Get customers by source
     * 
     * @param source the source
     * @param pageable the pageable object
     * @return page of customers
     */
    @Transactional(readOnly = true)
    public Page<Customer> getCustomersBySource(String source, Pageable pageable) {
        log.debug("Fetching customers by source: {}", source);
        return customerRepository.findBySource(source, pageable);
    }

    /**
     * Get customers by created by user
     * 
     * @param createdBy the user ID
     * @param pageable the pageable object
     * @return page of customers
     */
    @Transactional(readOnly = true)
    public Page<Customer> getCustomersByCreatedBy(Long createdBy, Pageable pageable) {
        log.debug("Fetching customers by created by: {}", createdBy);
        return customerRepository.findByCreatedBy(createdBy, pageable);
    }

    /**
     * Get customers by date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable the pageable object
     * @return page of customers
     */
    @Transactional(readOnly = true)
    public Page<Customer> getCustomersByDateRange(Date startDate, Date endDate, Pageable pageable) {
        log.debug("Fetching customers by date range: {} to {}", startDate, endDate);
        return customerRepository.findByCreatedAtBetween(startDate, endDate, pageable);
    }

    /**
     * Update customer
     * 
     * @param id the customer ID
     * @param customer the updated customer data
     * @param updatedBy the user ID who updated the customer
     * @return the updated customer
     */
    public Customer updateCustomer(Long id, Customer customer, String updatedBy) {
        log.info("Updating customer with ID: {}", id);
        
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        
        existingCustomer.setName(customer.getName());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setPhone(customer.getPhone());
        existingCustomer.setCompany(customer.getCompany());
        existingCustomer.setSource(customer.getSource());
        existingCustomer.setIsActive(customer.getIsActive());
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        log.info("Customer updated successfully with ID: {}", updatedCustomer.getId());
        
        return updatedCustomer;
    }

    /**
     * Change customer active status
     * 
     * @param id the customer ID
     * @param isActive the new active status
     * @param updatedBy the user ID who updated the customer
     * @return the updated customer
     */
    public Customer changeCustomerActiveStatus(Long id, Boolean isActive, String updatedBy) {
        log.info("Changing active status for customer {} to {}", id, isActive);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
        
        customer.setIsActive(isActive);
        
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer active status changed successfully for ID: {}", updatedCustomer.getId());
        
        return updatedCustomer;
    }

    /**
     * Delete customer
     * 
     * @param id the customer ID
     */
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with ID: " + id);
        }
        
        customerRepository.deleteById(id);
        log.info("Customer deleted successfully with ID: {}", id);
    }

    /**
     * Export customers
     * 
     * @param isActive the active status filter
     * @param company the company filter
     * @param source the source filter
     * @return list of customers
     */
    @Transactional(readOnly = true)
    public List<Customer> exportCustomers(Boolean isActive, String company, String source) {
        log.info("Exporting customers with filters: isActive={}, company={}, source={}", isActive, company, source);
        
        // This is a simplified export - in a real implementation, you might want to use a more sophisticated query
        return customerRepository.findAll();
    }

    /**
     * Count all customers
     * 
     * @return total count of customers
     */
    @Transactional(readOnly = true)
    public long countAllCustomers() {
        return customerRepository.count();
    }

    /**
     * Count customers by active status
     * 
     * @param isActive the active status
     * @return count of customers
     */
    @Transactional(readOnly = true)
    public long countCustomersByActiveStatus(Boolean isActive) {
        return customerRepository.countByIsActive(isActive);
    }

    /**
     * Count customers by company
     * 
     * @param company the company name
     * @return count of customers
     */
    @Transactional(readOnly = true)
    public long countCustomersByCompany(String company) {
        return customerRepository.countByCompany(company);
    }

    /**
     * Count customers by source
     * 
     * @param source the source
     * @return count of customers
     */
    @Transactional(readOnly = true)
    public long countCustomersBySource(String source) {
        return customerRepository.countBySource(source);
    }

    /**
     * Check if customer exists by email
     * 
     * @param email the email address
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    /**
     * Parse user ID from string
     * 
     * @param userId the user ID as string
     * @return the user ID as Long
     */
    private Long parseUserId(String userId) {
        try {
            return userId != null ? Long.parseLong(userId) : null;
        } catch (NumberFormatException e) {
            log.warn("Invalid user ID format: {}", userId);
            return null;
        }
    }

    /**
     * Get all active customers for dropdown
     * 
     * @return list of active customers
     */
    @Transactional(readOnly = true)
    public List<Customer> getAllActiveCustomers() {
        log.debug("Getting all active customers for dropdown");
        return customerRepository.findByIsActiveTrue();
    }
} 