package com.crm.user.repository;

import com.crm.user.entity.Role;
import com.crm.user.entity.User;
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
 * User Repository
 * 
 * Provides data access methods for User entities.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     * 
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Search users by name or email
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return Page of users matching the search term
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);


    /**
     * Find users who haven't logged in for a specified period
     * 
     * @param date the date to check from
     * @return List of users who haven't logged in since the date
     */
    List<User> findByLastLoginAtBeforeOrLastLoginAtIsNull(Date date);

    /**
     * Find users created after a specific date
     * 
     * @param date the date to search from
     * @return List of users created after the date
     */
    List<User> findByCreatedAtAfter(Date date);

    /**
     * Find all active and non-deleted users
     * 
     * @return List of active users
     */
    List<User> findByIsActiveTrueAndIsDeletedFalse();

    Page<User> findByIsDeletedFalse(Pageable pageable);

    Page<User> findByIsDeletedFalseAndIsActive(boolean isActive, Pageable pageable);
} 