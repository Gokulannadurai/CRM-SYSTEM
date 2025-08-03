package com.crm.customer.repository;

import com.crm.customer.entity.InteractionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Interaction History Repository
 * 
 * Repository interface for InteractionHistory entity operations.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Repository
public interface InteractionHistoryRepository extends JpaRepository<InteractionHistory, Long> {

    /**
     * Find interaction history by customer ID
     * 
     * @param customerId the customer ID
     * @param pageable the pageable object
     * @return page of interaction history
     */
    Page<InteractionHistory> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * Find interaction history by user ID
     * 
     * @param userId the user ID
     * @param pageable the pageable object
     * @return page of interaction history
     */
    Page<InteractionHistory> findByUserId(Long userId, Pageable pageable);

    /**
     * Find interaction history by interaction type
     * 
     * @param interactionType the interaction type
     * @param pageable the pageable object
     * @return page of interaction history
     */
    Page<InteractionHistory> findByInteractionType(String interactionType, Pageable pageable);

    /**
     * Find interaction history by customer ID and interaction type
     * 
     * @param customerId the customer ID
     * @param interactionType the interaction type
     * @param pageable the pageable object
     * @return page of interaction history
     */
    Page<InteractionHistory> findByCustomerIdAndInteractionType(Long customerId, String interactionType, Pageable pageable);

    /**
     * Find interaction history by interaction date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable the pageable object
     * @return page of interaction history
     */
    Page<InteractionHistory> findByInteractionDateBetween(Date startDate, Date endDate, Pageable pageable);

    /**
     * Find interaction history by customer ID and date range
     * 
     * @param customerId the customer ID
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable the pageable object
     * @return page of interaction history
     */
    Page<InteractionHistory> findByCustomerIdAndInteractionDateBetween(Long customerId, Date startDate, Date endDate, Pageable pageable);

    /**
     * Find recent interaction history for a customer
     * 
     * @param customerId the customer ID
     * @param pageable the pageable object
     * @return list of recent interaction history
     */
    @Query("SELECT ih FROM InteractionHistory ih WHERE ih.customerId = :customerId ORDER BY ih.interactionDate DESC")
    List<InteractionHistory> findRecentByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    /**
     * Count interaction history by customer ID
     * 
     * @param customerId the customer ID
     * @return count of interaction history
     */
    long countByCustomerId(Long customerId);

    /**
     * Count interaction history by interaction type
     * 
     * @param interactionType the interaction type
     * @return count of interaction history
     */
    long countByInteractionType(String interactionType);

    /**
     * Count interaction history by user ID
     * 
     * @param userId the user ID
     * @return count of interaction history
     */
    long countByUserId(Long userId);
} 