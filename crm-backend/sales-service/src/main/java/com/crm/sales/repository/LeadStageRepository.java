package com.crm.sales.repository;

import com.crm.sales.entity.LeadStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Lead Stage Repository
 * 
 * Repository interface for LeadStage entity operations.
 * Provides custom queries for lead stage management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Repository
public interface LeadStageRepository extends JpaRepository<LeadStage, Long> {

    /**
     * Find lead stage by name
     * 
     * @param name the lead stage name
     * @return optional lead stage
     */
    Optional<LeadStage> findByName(String name);

    /**
     * Find lead stage by name (case insensitive)
     * 
     * @param name the lead stage name
     * @return optional lead stage
     */
    Optional<LeadStage> findByNameIgnoreCase(String name);

    /**
     * Check if lead stage exists by name
     * 
     * @param name the lead stage name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if lead stage exists by name (case insensitive)
     * 
     * @param name the lead stage name
     * @return true if exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find lead stage by position
     * 
     * @param position the position
     * @return optional lead stage
     */
    Optional<LeadStage> findByPosition(Integer position);

    /**
     * Find all lead stages ordered by position ascending
     * 
     * @return list of lead stages
     */
    List<LeadStage> findAllByOrderByPositionAsc();

    /**
     * Find all lead stages ordered by position descending
     * 
     * @return list of lead stages
     */
    List<LeadStage> findAllByOrderByPositionDesc();

    /**
     * Find all lead stages with pagination ordered by position ascending
     * 
     * @param pageable pagination parameters
     * @return page of lead stages
     */
    Page<LeadStage> findAllByOrderByPositionAsc(Pageable pageable);

    /**
     * Find all lead stages with pagination ordered by position descending
     * 
     * @param pageable pagination parameters
     * @return page of lead stages
     */
    Page<LeadStage> findAllByOrderByPositionDesc(Pageable pageable);

    /**
     * Find lead stages by position greater than
     * 
     * @param position the position
     * @return list of lead stages
     */
    List<LeadStage> findByPositionGreaterThan(Integer position);

    /**
     * Find lead stages by position less than
     * 
     * @param position the position
     * @return list of lead stages
     */
    List<LeadStage> findByPositionLessThan(Integer position);

    /**
     * Find lead stages by position between
     * 
     * @param startPosition the start position
     * @param endPosition the end position
     * @return list of lead stages
     */
    List<LeadStage> findByPositionBetween(Integer startPosition, Integer endPosition);

    /**
     * Find the first stage (position = 1)
     * 
     * @return optional lead stage
     */
    Optional<LeadStage> findFirstByOrderByPositionAsc();

    /**
     * Find the last stage (highest position)
     * 
     * @return optional lead stage
     */
    Optional<LeadStage> findFirstByOrderByPositionDesc();
} 