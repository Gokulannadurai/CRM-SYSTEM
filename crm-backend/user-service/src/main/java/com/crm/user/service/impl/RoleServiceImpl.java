package com.crm.user.service.impl;

import com.crm.user.entity.Role;
import com.crm.user.entity.User;
import com.crm.user.repository.RoleRepository;
import com.crm.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Role Service Implementation
 * 
 * Implementation of RoleService interface providing business logic for Role entity.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role createRole(Role role) {
        log.info("Creating new role: {}", role.getName());
        
        if (roleRepository.existsByName(role.getName())) {
            log.warn("Role with name '{}' already exists", role.getName());
            throw new IllegalArgumentException("Role with name '" + role.getName() + "' already exists");
        }
        
        Role savedRole = roleRepository.save(role);
        log.info("Successfully created role: {} with ID: {}", savedRole.getName(), savedRole.getId());
        return savedRole;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(Long id) {
        log.debug("Finding role by ID: {}", id);
        return roleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByName(String name) {
        log.debug("Finding role by name: {}", name);
        return roleRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        log.debug("Retrieving all roles");
        return roleRepository.findAll();
    }

    @Override
    public Role updateRole(Long id, Role role) {
        log.info("Updating role with ID: {}", id);
        
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Role with ID {} not found", id);
                    return new IllegalArgumentException("Role with ID " + id + " not found");
                });
        
        // Check if name is being changed and if it conflicts with existing role
        if (!existingRole.getName().equals(role.getName()) && roleRepository.existsByName(role.getName())) {
            log.warn("Role with name '{}' already exists", role.getName());
            throw new IllegalArgumentException("Role with name '" + role.getName() + "' already exists");
        }
        
        existingRole.setName(role.getName());
        existingRole.setLevel(role.getLevel());
        
        Role updatedRole = roleRepository.save(existingRole);
        log.info("Successfully updated role: {} with ID: {}", updatedRole.getName(), updatedRole.getId());
        return updatedRole;
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Deleting role with ID: {}", id);
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Role with ID {} not found for deletion", id);
                    return new IllegalArgumentException("Role with ID " + id + " not found");
                });
        
        // Note: In a many-to-many relationship, we should check if the role is assigned to any users
        // This would require a separate query to check user-role relationships
        // For now, we'll allow deletion and let the database handle foreign key constraints
        
        roleRepository.deleteById(id);
        log.info("Successfully deleted role: {} with ID: {}", role.getName(), id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findByLevel(Integer level) {
        log.debug("Finding roles by level: {}", level);
        return roleRepository.findByLevel(level);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findByLevelGreaterThanEqual(Integer level) {
        log.debug("Finding roles with level >= {}", level);
        return roleRepository.findByLevelGreaterThanEqual(level);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findByLevelLessThanEqual(Integer level) {
        log.debug("Finding roles with level <= {}", level);
        return roleRepository.findByLevelLessThanEqual(level);
    }
} 