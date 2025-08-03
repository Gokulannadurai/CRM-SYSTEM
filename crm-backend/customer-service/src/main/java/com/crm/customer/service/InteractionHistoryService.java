package com.crm.customer.service;

import com.crm.customer.entity.InteractionHistory;
import com.crm.customer.repository.InteractionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Interaction History Service
 * 
 * Service layer for InteractionHistory entity business logic.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InteractionHistoryService {

    private final InteractionHistoryRepository interactionHistoryRepository;

    /**
     * Create a new interaction history record
     * 
     * @param interactionHistory the interaction history to create
     * @return the created interaction history
     */
    public InteractionHistory createInteractionHistory(InteractionHistory interactionHistory) {
        log.info("Creating new interaction history for customer: {}", interactionHistory.getCustomerId());
        
        if (interactionHistory.getInteractionDate() == null) {
            interactionHistory.setInteractionDate(new Date());
        }
        
        InteractionHistory savedInteractionHistory = interactionHistoryRepository.save(interactionHistory);
        log.info("Interaction history created successfully with ID: {}", savedInteractionHistory.getId());
        
        return savedInteractionHistory;
    }

    /**
     * Get interaction history by ID
     * 
     * @param id the interaction history ID
     * @return optional interaction history
     */
    @Transactional(readOnly = true)
    public Optional<InteractionHistory> getInteractionHistoryById(Long id) {
        log.debug("Fetching interaction history by ID: {}", id);
        return interactionHistoryRepository.findById(id);
    }

    /**
     * Get interaction history by customer ID
     * 
     * @param customerId the customer ID
     * @param pageable the pageable object
     * @return page of interaction history
     */
    @Transactional(readOnly = true)
    public Page<InteractionHistory> getInteractionHistoryByCustomerId(Long customerId, Pageable pageable) {
        log.debug("Fetching interaction history by customer ID: {}", customerId);
        return interactionHistoryRepository.findByCustomerId(customerId, pageable);
    }

    /**
     * Get interaction history by user ID
     * 
     * @param userId the user ID
     * @param pageable the pageable object
     * @return page of interaction history
     */
    @Transactional(readOnly = true)
    public Page<InteractionHistory> getInteractionHistoryByUserId(Long userId, Pageable pageable) {
        log.debug("Fetching interaction history by user ID: {}", userId);
        return interactionHistoryRepository.findByUserId(userId, pageable);
    }

    /**
     * Get interaction history by interaction type
     * 
     * @param interactionType the interaction type
     * @param pageable the pageable object
     * @return page of interaction history
     */
    @Transactional(readOnly = true)
    public Page<InteractionHistory> getInteractionHistoryByType(String interactionType, Pageable pageable) {
        log.debug("Fetching interaction history by type: {}", interactionType);
        return interactionHistoryRepository.findByInteractionType(interactionType, pageable);
    }

    /**
     * Get interaction history by customer ID and interaction type
     * 
     * @param customerId the customer ID
     * @param interactionType the interaction type
     * @param pageable the pageable object
     * @return page of interaction history
     */
    @Transactional(readOnly = true)
    public Page<InteractionHistory> getInteractionHistoryByCustomerIdAndType(Long customerId, String interactionType, Pageable pageable) {
        log.debug("Fetching interaction history by customer ID: {} and type: {}", customerId, interactionType);
        return interactionHistoryRepository.findByCustomerIdAndInteractionType(customerId, interactionType, pageable);
    }

    /**
     * Get interaction history by date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable the pageable object
     * @return page of interaction history
     */
    @Transactional(readOnly = true)
    public Page<InteractionHistory> getInteractionHistoryByDateRange(Date startDate, Date endDate, Pageable pageable) {
        log.debug("Fetching interaction history by date range: {} to {}", startDate, endDate);
        return interactionHistoryRepository.findByInteractionDateBetween(startDate, endDate, pageable);
    }

    /**
     * Get interaction history by customer ID and date range
     * 
     * @param customerId the customer ID
     * @param startDate the start date
     * @param endDate the end date
     * @param pageable the pageable object
     * @return page of interaction history
     */
    @Transactional(readOnly = true)
    public Page<InteractionHistory> getInteractionHistoryByCustomerIdAndDateRange(Long customerId, Date startDate, Date endDate, Pageable pageable) {
        log.debug("Fetching interaction history by customer ID: {} and date range: {} to {}", customerId, startDate, endDate);
        return interactionHistoryRepository.findByCustomerIdAndInteractionDateBetween(customerId, startDate, endDate, pageable);
    }

    /**
     * Get recent interaction history for a customer
     * 
     * @param customerId the customer ID
     * @param limit the limit of records
     * @return list of recent interaction history
     */
    @Transactional(readOnly = true)
    public List<InteractionHistory> getRecentInteractionHistoryByCustomerId(Long customerId, int limit) {
        log.debug("Fetching recent interaction history for customer ID: {} with limit: {}", customerId, limit);
        Pageable pageable = PageRequest.of(0, limit);
        return interactionHistoryRepository.findRecentByCustomerId(customerId, pageable);
    }

    /**
     * Update interaction history
     * 
     * @param id the interaction history ID
     * @param interactionHistory the updated interaction history data
     * @return the updated interaction history
     */
    public InteractionHistory updateInteractionHistory(Long id, InteractionHistory interactionHistory) {
        log.info("Updating interaction history with ID: {}", id);
        
        InteractionHistory existingInteractionHistory = interactionHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interaction history not found with ID: " + id));
        
        existingInteractionHistory.setCustomerId(interactionHistory.getCustomerId());
        existingInteractionHistory.setUserId(interactionHistory.getUserId());
        existingInteractionHistory.setInteractionType(interactionHistory.getInteractionType());
        existingInteractionHistory.setNotes(interactionHistory.getNotes());
        existingInteractionHistory.setInteractionDate(interactionHistory.getInteractionDate());
        
        InteractionHistory updatedInteractionHistory = interactionHistoryRepository.save(existingInteractionHistory);
        log.info("Interaction history updated successfully with ID: {}", updatedInteractionHistory.getId());
        
        return updatedInteractionHistory;
    }

    /**
     * Delete interaction history
     * 
     * @param id the interaction history ID
     */
    public void deleteInteractionHistory(Long id) {
        log.info("Deleting interaction history with ID: {}", id);
        
        if (!interactionHistoryRepository.existsById(id)) {
            throw new RuntimeException("Interaction history not found with ID: " + id);
        }
        
        interactionHistoryRepository.deleteById(id);
        log.info("Interaction history deleted successfully with ID: {}", id);
    }

    /**
     * Count interaction history by customer ID
     * 
     * @param customerId the customer ID
     * @return count of interaction history
     */
    @Transactional(readOnly = true)
    public long countInteractionHistoryByCustomerId(Long customerId) {
        return interactionHistoryRepository.countByCustomerId(customerId);
    }

    /**
     * Count interaction history by interaction type
     * 
     * @param interactionType the interaction type
     * @return count of interaction history
     */
    @Transactional(readOnly = true)
    public long countInteractionHistoryByType(String interactionType) {
        return interactionHistoryRepository.countByInteractionType(interactionType);
    }

    /**
     * Count interaction history by user ID
     * 
     * @param userId the user ID
     * @return count of interaction history
     */
    @Transactional(readOnly = true)
    public long countInteractionHistoryByUserId(Long userId) {
        return interactionHistoryRepository.countByUserId(userId);
    }
} 