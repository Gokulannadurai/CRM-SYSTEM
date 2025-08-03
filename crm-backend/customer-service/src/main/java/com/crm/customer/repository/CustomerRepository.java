package com.crm.customer.repository;

import com.crm.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Customer Repository
 * 
 * Repository interface for Customer entity operations.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customer by email
     * 
     * @param email the email address
     * @return optional customer
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Find customers by active status
     * 
     * @param isActive the active status
     * @param pageable the pageable object
     * @return page of customers
     */
    Page<Customer> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Find customers by company
     * 
     * @param company the company name
     * @param pageable the pageable object
     * @return page of customers
     */
    Page<Customer> findByCompany(String company, Pageable pageable);

    /**
     * Find customers by source
     * 
     * @param source the source
     * @param pageable the pageable object
     * @return page of customers
     */
    Page<Customer> findBySource(String source, Pageable pageable);

    /**
     * Find customers by created by user
     * 
     * @param createdBy the user ID
     * @param pageable the pageable object
     * @return page of customers
     */
    Page<Customer> findByCreatedBy(Long createdBy, Pageable pageable);

    /**
     * Find customers by creation date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable the pageable object
     * @return page of customers
     */
    Page<Customer> findByCreatedAtBetween(Date startDate, Date endDate, Pageable pageable);

    /**
     * Search customers by name or email
     * 
     * @param searchTerm the search term
     * @param pageable the pageable object
     * @return page of customers
     */
    @Query("SELECT c FROM Customer c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.company) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Customer> searchCustomers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count customers by active status
     * 
     * @param isActive the active status
     * @return count of customers
     */
    long countByIsActive(Boolean isActive);

    /**
     * Count customers by company
     * 
     * @param company the company name
     * @return count of customers
     */
    long countByCompany(String company);

    /**
     * Count customers by source
     * 
     * @param source the source
     * @return count of customers
     */
    long countBySource(String source);

    /**
     * Check if customer exists by email
     * 
     * @param email the email address
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find all active customers
     * 
     * @return list of active customers
     */
    List<Customer> findByIsActiveTrue();
} 