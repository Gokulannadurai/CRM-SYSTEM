package com.crm.user.controller;

import com.crm.user.dto.RoleDto;
import com.crm.user.entity.Role;
import com.crm.user.mapper.RoleMapper;
import com.crm.user.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Role Controller
 * 
 * REST controller for managing Role entities.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    /**
     * Create a new role
     * 
     * @param roleDto the role data
     * @return the created role
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        log.info("Creating new role: {}", roleDto.getName());
        
        Role role = roleMapper.toEntity(roleDto);
        Role createdRole = roleService.createRole(role);
        RoleDto createdRoleDto = roleMapper.toDto(createdRole);
        
        log.info("Successfully created role with ID: {}", createdRole.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoleDto);
    }

    /**
     * Get role by ID
     * 
     * @param id the role ID
     * @return the role
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        log.debug("Getting role by ID: {}", id);
        
        return roleService.findById(id)
                .map(role -> {
                    RoleDto roleDto = roleMapper.toDto(role);
                    return ResponseEntity.ok(roleDto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get role by name
     * 
     * @param name the role name
     * @return the role
     */
    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> getRoleByName(@PathVariable String name) {
        log.debug("Getting role by name: {}", name);
        
        return roleService.findByName(name)
                .map(role -> {
                    RoleDto roleDto = roleMapper.toDto(role);
                    return ResponseEntity.ok(roleDto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all roles
     * 
     * @return list of all roles
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        log.debug("Getting all roles");
        
        List<Role> roles = roleService.getAllRoles();
        List<RoleDto> roleDtos = roleMapper.toDtoList(roles);
        
        return ResponseEntity.ok(roleDtos);
    }

    /**
     * Update role
     * 
     * @param id the role ID
     * @param roleDto the updated role data
     * @return the updated role
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDto roleDto) {
        log.info("Updating role with ID: {}", id);
        
        Role role = roleMapper.toEntity(roleDto);
        Role updatedRole = roleService.updateRole(id, role);
        RoleDto updatedRoleDto = roleMapper.toDto(updatedRole);
        
        log.info("Successfully updated role with ID: {}", id);
        return ResponseEntity.ok(updatedRoleDto);
    }

    /**
     * Delete role
     * 
     * @param id the role ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        log.info("Deleting role with ID: {}", id);
        
        roleService.deleteRole(id);
        
        log.info("Successfully deleted role with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get roles by level
     * 
     * @param level the role level
     * @return list of roles
     */
    @GetMapping("/level/{level}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleDto>> getRolesByLevel(@PathVariable Integer level) {
        log.debug("Getting roles by level: {}", level);
        
        List<Role> roles = roleService.findByLevel(level);
        List<RoleDto> roleDtos = roleMapper.toDtoList(roles);
        
        return ResponseEntity.ok(roleDtos);
    }

    /**
     * Get roles with level greater than or equal to specified level
     * 
     * @param level the minimum level
     * @return list of roles
     */
    @GetMapping("/level/gte/{level}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleDto>> getRolesByLevelGreaterThanEqual(@PathVariable Integer level) {
        log.debug("Getting roles with level >= {}", level);
        
        List<Role> roles = roleService.findByLevelGreaterThanEqual(level);
        List<RoleDto> roleDtos = roleMapper.toDtoList(roles);
        
        return ResponseEntity.ok(roleDtos);
    }

    /**
     * Get roles with level less than or equal to specified level
     * 
     * @param level the maximum level
     * @return list of roles
     */
    @GetMapping("/level/lte/{level}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleDto>> getRolesByLevelLessThanEqual(@PathVariable Integer level) {
        log.debug("Getting roles with level <= {}", level);
        
        List<Role> roles = roleService.findByLevelLessThanEqual(level);
        List<RoleDto> roleDtos = roleMapper.toDtoList(roles);
        
        return ResponseEntity.ok(roleDtos);
    }
} 