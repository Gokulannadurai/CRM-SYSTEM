package com.crm.sales.repository;

import com.crm.sales.entity.TaskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Task Type Repository
 * 
 * Repository interface for TaskType entity operations.
 * Provides custom queries for task type management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Repository
public interface TaskTypeRepository extends JpaRepository<TaskType, Long> {

    /**
     * Find task type by name
     * 
     * @param name the task type name
     * @return optional task type
     */
    Optional<TaskType> findByName(String name);

    /**
     * Find task type by name (case insensitive)
     * 
     * @param name the task type name
     * @return optional task type
     */
    Optional<TaskType> findByNameIgnoreCase(String name);

    /**
     * Check if task type exists by name
     * 
     * @param name the task type name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if task type exists by name (case insensitive)
     * 
     * @param name the task type name
     * @return true if exists, false otherwise
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find task types by name containing (case insensitive)
     * 
     * @param name the name to search for
     * @return list of task types
     */
    List<TaskType> findByNameContainingIgnoreCase(String name);

    /**
     * Find task types by name containing with pagination
     * 
     * @param name the name to search for
     * @param pageable pagination parameters
     * @return page of task types
     */
    Page<TaskType> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find all task types ordered by name
     * 
     * @return list of task types
     */
    List<TaskType> findAllByOrderByNameAsc();

    /**
     * Find all task types with pagination ordered by name
     * 
     * @param pageable pagination parameters
     * @return page of task types
     */
    Page<TaskType> findAllByOrderByNameAsc(Pageable pageable);
} 