package com.crm.sales.mapper;

import com.crm.sales.dto.LeadStageDto;
import com.crm.sales.entity.LeadStage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Lead Stage Mapper
 * 
 * Custom mapper for converting between LeadStage entity and LeadStageDto.
 * Follows the same pattern as customer-service mappers.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Component
public class LeadStageMapper {

    /**
     * Convert LeadStage entity to LeadStageDto
     * 
     * @param leadStage the lead stage entity
     * @return the lead stage DTO
     */
    public LeadStageDto toDto(LeadStage leadStage) {
        if (leadStage == null) {
            return null;
        }

        LeadStageDto dto = new LeadStageDto();
        dto.setId(leadStage.getId());
        dto.setName(leadStage.getName());
        dto.setPosition(leadStage.getPosition());
        dto.setCreatedAt(leadStage.getCreatedAt());
        dto.setUpdatedAt(leadStage.getUpdatedAt());

        return dto;
    }

    /**
     * Convert LeadStageDto to LeadStage entity
     * 
     * @param dto the lead stage DTO
     * @return the lead stage entity
     */
    public LeadStage toEntity(LeadStageDto dto) {
        if (dto == null) {
            return null;
        }

        LeadStage leadStage = new LeadStage();
        leadStage.setId(dto.getId());
        leadStage.setName(dto.getName());
        leadStage.setPosition(dto.getPosition());

        return leadStage;
    }

    /**
     * Convert list of LeadStage entities to list of LeadStageDto
     * 
     * @param leadStages the list of lead stage entities
     * @return the list of lead stage DTOs
     */
    public List<LeadStageDto> toDtoList(List<LeadStage> leadStages) {
        if (leadStages == null) {
            return null;
        }

        return leadStages.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert list of LeadStageDto to list of LeadStage entities
     * 
     * @param dtos the list of lead stage DTOs
     * @return the list of lead stage entities
     */
    public List<LeadStage> toEntityList(List<LeadStageDto> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 