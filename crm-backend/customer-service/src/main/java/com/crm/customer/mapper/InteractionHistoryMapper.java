package com.crm.customer.mapper;

import com.crm.customer.dto.InteractionHistoryDto;
import com.crm.customer.entity.InteractionHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Interaction History Mapper
 * 
 * Maps between InteractionHistory entity and InteractionHistoryDto.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Component
public class InteractionHistoryMapper {

    /**
     * Map InteractionHistory entity to InteractionHistoryDto
     * 
     * @param interactionHistory the interaction history entity
     * @return the interaction history DTO
     */
    public InteractionHistoryDto toDto(InteractionHistory interactionHistory) {
        if (interactionHistory == null) {
            return null;
        }

        InteractionHistoryDto interactionHistoryDto = new InteractionHistoryDto();
        interactionHistoryDto.setId(interactionHistory.getId());
        interactionHistoryDto.setCustomerId(interactionHistory.getCustomerId());
        interactionHistoryDto.setUserId(interactionHistory.getUserId());
        interactionHistoryDto.setInteractionType(interactionHistory.getInteractionType());
        interactionHistoryDto.setNotes(interactionHistory.getNotes());
        interactionHistoryDto.setInteractionDate(interactionHistory.getInteractionDate());
        interactionHistoryDto.setCreatedAt(interactionHistory.getCreatedAt());
        interactionHistoryDto.setUpdatedAt(interactionHistory.getUpdatedAt());

        return interactionHistoryDto;
    }

    /**
     * Map InteractionHistoryDto to InteractionHistory entity
     * 
     * @param interactionHistoryDto the interaction history DTO
     * @return the interaction history entity
     */
    public InteractionHistory toEntity(InteractionHistoryDto interactionHistoryDto) {
        if (interactionHistoryDto == null) {
            return null;
        }

        InteractionHistory interactionHistory = new InteractionHistory();
        interactionHistory.setId(interactionHistoryDto.getId());
        interactionHistory.setCustomerId(interactionHistoryDto.getCustomerId());
        interactionHistory.setUserId(interactionHistoryDto.getUserId());
        interactionHistory.setInteractionType(interactionHistoryDto.getInteractionType());
        interactionHistory.setNotes(interactionHistoryDto.getNotes());
        interactionHistory.setInteractionDate(interactionHistoryDto.getInteractionDate());
        interactionHistory.setCreatedAt(interactionHistoryDto.getCreatedAt());
        interactionHistory.setUpdatedAt(interactionHistoryDto.getUpdatedAt());

        return interactionHistory;
    }

    /**
     * Map list of InteractionHistory entities to list of InteractionHistoryDto
     * 
     * @param interactionHistories the list of interaction history entities
     * @return the list of interaction history DTOs
     */
    public List<InteractionHistoryDto> toDtoList(List<InteractionHistory> interactionHistories) {
        if (interactionHistories == null) {
            return null;
        }

        return interactionHistories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Map list of InteractionHistoryDto to list of InteractionHistory entities
     * 
     * @param interactionHistoryDtos the list of interaction history DTOs
     * @return the list of interaction history entities
     */
    public List<InteractionHistory> toEntityList(List<InteractionHistoryDto> interactionHistoryDtos) {
        if (interactionHistoryDtos == null) {
            return null;
        }

        return interactionHistoryDtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 