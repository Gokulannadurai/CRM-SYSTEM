package com.crm.user.repository;

import com.crm.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Role Repository
 * 
 * Repository interface for Role entity providing data access operations.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name
     * 
     * @param name the role name
     * @return optional role
     */
    Optional<Role> findByName(String name);

    /**
     * Check if role exists by name
     * 
     * @param name the role name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Find roles by level
     * 
     * @param level the role level
     * @return list of roles
     */
    List<Role> findByLevel(Integer level);

    /**
     * Find roles with level greater than or equal to specified level
     * 
     * @param level the minimum level
     * @return list of roles
     */
    @Query("SELECT r FROM Role r WHERE r.level >= :level ORDER BY r.level ASC")
    List<Role> findByLevelGreaterThanEqual(@Param("level") Integer level);

    /**
     * Find roles with level less than or equal to specified level
     * 
     * @param level the maximum level
     * @return list of roles
     */
    @Query("SELECT r FROM Role r WHERE r.level <= :level ORDER BY r.level DESC")
    List<Role> findByLevelLessThanEqual(@Param("level") Integer level);

    List<Role> findAllById(Iterable<Long> ids);

} 