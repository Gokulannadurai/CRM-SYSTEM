package com.crm.sales.repository;

import com.crm.sales.entity.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Lead Repository
 * 
 * Repository interface for Lead entity operations.
 * Provides custom queries for lead management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    /**
     * Find leads by customer ID
     * 
     * @param customerId the customer ID
     * @return list of leads
     */
    List<Lead> findByCustomerId(Long customerId);

    /**
     * Find leads by assigned user
     * 
     * @param assignedTo the assigned user ID
     * @return list of leads
     */
    List<Lead> findByAssignedTo(Long assignedTo);

    /**
     * Find leads by status
     * 
     * @param status the lead status
     * @return list of leads
     */
    List<Lead> findByStatus(String status);

    /**
     * Find leads by current stage
     * 
     * @param currentStageId the current stage ID
     * @return list of leads
     */
    List<Lead> findByCurrentStageId(Long currentStageId);

    /**
     * Find overdue leads
     * 
     * @return list of overdue leads
     */
    @Query("SELECT l FROM Lead l WHERE l.expectedCloseDate < :today")
    List<Lead> findOverdueLeads(@Param("today") Date today);

    /**
     * Find leads due soon (within specified days)
     * 
     * @param today current date
     * @param daysFromNow number of days from now
     * @return list of leads due soon
     */
    @Query("SELECT l FROM Lead l WHERE l.expectedCloseDate BETWEEN :today AND :daysFromNow")
    List<Lead> findLeadsDueSoon(@Param("today") Date today,
                                @Param("daysFromNow") Date daysFromNow);

    /**
     * Find leads by assigned user with pagination
     * 
     * @param assignedTo the assigned user ID
     * @param pageable pagination parameters
     * @return page of leads
     */
    Page<Lead> findByAssignedTo(Long assignedTo, Pageable pageable);

    /**
     * Find leads by status with pagination
     * 
     * @param status the lead status
     * @param pageable pagination parameters
     * @return page of leads
     */
    Page<Lead> findByStatus(String status, Pageable pageable);

    /**
     * Find leads by current stage with pagination
     * 
     * @param currentStageId the current stage ID
     * @param pageable pagination parameters
     * @return page of leads
     */
    Page<Lead> findByCurrentStageId(Long currentStageId, Pageable pageable);

    /**
     * Search leads by customer ID and status
     * 
     * @param customerId the customer ID
     * @param status the lead status
     * @return list of leads
     */
    List<Lead> findByCustomerIdAndStatus(Long customerId, String status);

    /**
     * Count leads by status
     * 
     * @param status the lead status
     * @return count of leads
     */
    long countByStatus(String status);

    /**
     * Count leads by assigned user
     * 
     * @param assignedTo the assigned user ID
     * @return count of leads
     */
    long countByAssignedTo(Long assignedTo);

    /**
     * Count leads by current stage
     * 
     * @param currentStageId the current stage ID
     * @return count of leads
     */
    long countByCurrentStageId(Long currentStageId);
} 