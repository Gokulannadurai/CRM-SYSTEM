package com.crm.user.mapper;

import com.crm.user.dto.RoleDto;
import com.crm.user.entity.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Role Mapper
 * 
 * Mapper class for converting between Role entity and RoleDto with proper null checks.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@Slf4j
@Component
public class RoleMapper {

    /**
     * Convert Role entity to RoleDto
     * 
     * @param role the role entity
     * @return the role DTO
     */
    public RoleDto toDto(Role role) {
        if (role == null) {
            log.warn("Role entity is null, returning null DTO");
            return null;
        }

        log.debug("Converting Role entity to DTO: {}", role.getName());
        
        RoleDto roleDto = new RoleDto();
        
        // Set ID with null check
        if (role.getId() != null) {
            roleDto.setId(role.getId());
        }
        
        // Set name with null check
        if (role.getName() != null) {
            roleDto.setName(role.getName());
        }
        
        // Set level with null check
        if (role.getLevel() != null) {
            roleDto.setLevel(role.getLevel());
        }
        
        // Set timestamps with null checks
        if (role.getCreatedAt() != null) {
            roleDto.setCreatedAt(role.getCreatedAt());
        }
        
        if (role.getUpdatedAt() != null) {
            roleDto.setUpdatedAt(role.getUpdatedAt());
        }
        
        log.debug("Successfully converted Role entity to DTO: {}", roleDto.getName());
        return roleDto;
    }

    /**
     * Convert RoleDto to Role entity
     * 
     * @param roleDto the role DTO
     * @return the role entity
     */
    public Role toEntity(RoleDto roleDto) {
        if (roleDto == null) {
            log.warn("Role DTO is null, returning null entity");
            return null;
        }

        log.debug("Converting Role DTO to entity: {}", roleDto.getName());
        
        Role role = new Role();
        
        // Set ID with null check
        if (roleDto.getId() != null) {
            role.setId(roleDto.getId());
        }
        
        // Set name with null check
        if (roleDto.getName() != null) {
            role.setName(roleDto.getName());
        }
        
        // Set level with null check
        if (roleDto.getLevel() != null) {
            role.setLevel(roleDto.getLevel());
        }
        
        // Set timestamps with null checks
        if (roleDto.getCreatedAt() != null) {
            role.setCreatedAt(roleDto.getCreatedAt());
        }
        
        if (roleDto.getUpdatedAt() != null) {
            role.setUpdatedAt(roleDto.getUpdatedAt());
        }
        
        // Note: Users are not set in toEntity to avoid circular dependency
        // Users should be managed separately through service layer
        
        log.debug("Successfully converted Role DTO to entity: {}", role.getName());
        return role;
    }

    /**
     * Update Role entity from RoleDto
     * 
     * @param roleDto the role DTO
     * @param role the role entity to update
     */
    public void updateEntityFromDto(RoleDto roleDto, Role role) {
        if (roleDto == null) {
            log.warn("Role DTO is null, cannot update entity");
            return;
        }
        
        if (role == null) {
            log.warn("Role entity is null, cannot update");
            return;
        }

        log.debug("Updating Role entity from DTO: {}", roleDto.getName());
        
        // Update name with null check
        if (roleDto.getName() != null) {
            role.setName(roleDto.getName());
        }
        
        // Update level with null check
        if (roleDto.getLevel() != null) {
            role.setLevel(roleDto.getLevel());
        }
        
        // Note: ID, timestamps, and users are not updated to maintain data integrity
        // ID should not be changed
        // Timestamps are managed by JPA
        // Users should be managed separately through service layer
        
        log.debug("Successfully updated Role entity from DTO: {}", role.getName());
    }

    /**
     * Convert list of Role entities to list of RoleDto
     * 
     * @param roles the list of role entities
     * @return the list of role DTOs
     */
    public List<RoleDto> toDtoList(List<Role> roles) {
        if (roles == null) {
            log.warn("Role list is null, returning empty list");
            return List.of();
        }

        log.debug("Converting {} Role entities to DTOs", roles.size());
        
        return roles.stream()
                .map(this::toDto)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of RoleDto to list of Role entities
     * 
     * @param roleDtos the list of role DTOs
     * @return the list of role entities
     */
    public List<Role> toEntityList(List<RoleDto> roleDtos) {
        if (roleDtos == null) {
            log.warn("Role DTO list is null, returning empty list");
            return List.of();
        }

        log.debug("Converting {} Role DTOs to entities", roleDtos.size());
        
        return roleDtos.stream()
                .map(this::toEntity)
                .filter(entity -> entity != null)
                .collect(Collectors.toList());
    }
} 