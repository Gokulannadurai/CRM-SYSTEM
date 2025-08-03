package com.crm.user.service;

import com.crm.user.entity.Role;
import com.crm.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Role Service
 * 
 * Service interface for Role entity providing business logic operations.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
public interface RoleService {

    /**
     * Create a new role
     * 
     * @param role the role to create
     * @return the created role
     */
    Role createRole(Role role);

    /**
     * Find role by ID
     * 
     * @param id the role ID
     * @return optional role
     */
    Optional<Role> findById(Long id);

    /**
     * Find role by name
     * 
     * @param name the role name
     * @return optional role
     */
    Optional<Role> findByName(String name);

    /**
     * Get all roles
     * 
     * @return list of all roles
     */
    List<Role> getAllRoles();

    /**
     * Update role
     * 
     * @param id the role ID
     * @param role the updated role data
     * @return the updated role
     */
    Role updateRole(Long id, Role role);

    /**
     * Delete role
     * 
     * @param id the role ID
     */
    void deleteRole(Long id);

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
    List<Role> findByLevelGreaterThanEqual(Integer level);

    /**
     * Find roles with level less than or equal to specified level
     * 
     * @param level the maximum level
     * @return list of roles
     */
    List<Role> findByLevelLessThanEqual(Integer level);
} 