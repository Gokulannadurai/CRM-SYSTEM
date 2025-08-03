package com.crm.user.mapper;

import com.crm.user.dto.RoleDto;
import com.crm.user.dto.UserDto;
import com.crm.user.entity.Role;
import com.crm.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User Mapper
 * 
 * Mapper class for converting between User entity and UserDto with proper null checks.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@Slf4j
@Component
public class UserMapper {

    /**
     * Convert User entity to UserDto
     * 
     * @param user the user entity
     * @return the user DTO
     */
    public UserDto toDto(User user) {
        if (user == null) {
            log.warn("User entity is null, returning null DTO");
            return null;
        }

        log.debug("Converting User entity to DTO: {}", user.getUsername());
        
        UserDto userDto = new UserDto();
        
        // Set ID with null check
        if (user.getId() != null) {
            userDto.setUserId(user.getId());
        }
        
        // Set username with null check
        if (user.getUsername() != null) {
            userDto.setUsername(user.getUsername());
        }
        
        // Set email with null check
        if (user.getEmail() != null) {
            userDto.setEmail(user.getEmail());
        }
        
        // Set first name with null check
        if (user.getFirstName() != null) {
            userDto.setFirstName(user.getFirstName());
        }
        
        // Set last name with null check
        if (user.getLastName() != null) {
            userDto.setLastName(user.getLastName());
        }
        
        // Set mobile number with null check
        if (user.getMobileNumber() != null) {
            userDto.setMobileNumber(user.getMobileNumber());
        }
        
        // Set active status with null check
        if (user.getIsActive() != null) {
            userDto.setIsActive(user.getIsActive());
        }
        
        // Set deleted status with null check
        if (user.getIsDeleted() != null) {
            userDto.setIsDeleted(user.getIsDeleted());
        }
        
        if (user.getLastLoginAt() != null) {
            userDto.setLastLoginAt(user.getLastLoginAt());
        }
        
        if (user.getCreatedAt() != null) {
            userDto.setCreatedAt(user.getCreatedAt());
        }
        
        if (user.getUpdatedAt() != null) {
            userDto.setUpdatedAt(user.getUpdatedAt());
        }

        Set<Role> roles = Optional.ofNullable(user.getRoles()).orElse(Collections.emptySet());
        userDto.setRoles(new ArrayList<>());
        for (Role role : roles) {
            userDto.getRoles().add(new RoleDto(role.getId(), role.getName()));
        }
        
        log.debug("Successfully converted User entity to DTO: {}", userDto.getUsername());
        return userDto;
    }

    /**
     * Convert UserDto to User entity
     * 
     * @param userDto the user DTO
     * @return the user entity
     */
    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            log.warn("User DTO is null, returning null entity");
            return null;
        }

        log.debug("Converting User DTO to entity: {}", userDto.getUsername());
        
        User user = new User();
        
        // Set ID with null check
        if (userDto.getUserId() != null) {
            user.setId(userDto.getUserId());
        }
        
        // Set username with null check
        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        
        // Set email with null check
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        
        // Set password hash with null check
        if (userDto.getPassword() != null) {
            user.setPassword(userDto.getPassword());
        }
        
        // Set first name with null check
        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        
        // Set last name with null check
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }
        
        // Set mobile number with null check
        if (userDto.getMobileNumber() != null) {
            user.setMobileNumber(userDto.getMobileNumber());
        }

        // Set active status with null check
        if (userDto.getIsActive() != null) {
            user.setIsActive(userDto.getIsActive());
        }
        
        // Set deleted status with null check
        if (userDto.getIsDeleted() != null) {
            user.setIsDeleted(userDto.getIsDeleted());
        }

        // Set last login with null check
        if (userDto.getLastLoginAt() != null) {
            user.setLastLoginAt(userDto.getLastLoginAt());
        }
        
        // Set timestamps with null checks
        if (userDto.getCreatedAt() != null) {
            user.setCreatedAt(userDto.getCreatedAt());
        }
        
        if (userDto.getUpdatedAt() != null) {
            user.setUpdatedAt(userDto.getUpdatedAt());
        }
        List<RoleDto> roles = Optional.ofNullable(userDto.getRoles()).orElse(Collections.emptyList());
        Set<Role> userRoles = new HashSet<>();
        for (RoleDto roleDto : roles) {
            if (roleDto.getId() != null && roleDto.getName() != null) {
                userRoles.add(new Role(roleDto.getId(), roleDto.getName()));
            }
        }
        user.setRoles(userRoles);
        log.debug("Successfully converted User DTO to entity: {}", user.getUsername());
        return user;
    }

    /**
     * Convert list of User entities to list of UserDto
     * 
     * @param users the list of user entities
     * @return the list of user DTOs
     */
    public List<UserDto> toDtoList(List<User> users) {
        if (users == null) {
            log.warn("User list is null, returning empty list");
            return List.of();
        }

        log.debug("Converting {} User entities to DTOs", users.size());
        
        return users.stream()
                .map(this::toDto)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of UserDto to list of User entities
     * 
     * @param userDtos the list of user DTOs
     * @return the list of user entities
     */
    public List<User> toEntityList(List<UserDto> userDtos) {
        if (userDtos == null) {
            log.warn("User DTO list is null, returning empty list");
            return List.of();
        }

        log.debug("Converting {} User DTOs to entities", userDtos.size());
        
        return userDtos.stream()
                .map(this::toEntity)
                .filter(entity -> entity != null)
                .collect(Collectors.toList());
    }
} 