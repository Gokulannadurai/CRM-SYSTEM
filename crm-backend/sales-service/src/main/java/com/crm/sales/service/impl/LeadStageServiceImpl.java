package com.crm.sales.service.impl;

import com.crm.sales.dto.LeadStageDto;
import com.crm.sales.entity.LeadStage;
import com.crm.sales.mapper.LeadStageMapper;
import com.crm.sales.repository.LeadStageRepository;
import com.crm.sales.service.LeadStageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lead Stage Service Implementation
 * 
 * Implementation of LeadStageService interface with business logic for lead stage management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LeadStageServiceImpl implements LeadStageService {

    private final LeadStageRepository leadStageRepository;
    
    private final LeadStageMapper leadStageMapper;

    @Override
    public LeadStageDto createLeadStage(LeadStageDto leadStageDto) {
        log.info("Creating new lead stage: {}", leadStageDto.getName());
        
        // Check if lead stage already exists by name
        if (leadStageRepository.existsByNameIgnoreCase(leadStageDto.getName())) {
            throw new RuntimeException("Lead stage with name '" + leadStageDto.getName() + "' already exists");
        }

        // Check if position is already taken
        if (leadStageDto.getPosition() != null &&
            leadStageRepository.findByPosition(leadStageDto.getPosition()).isPresent()) {
            throw new RuntimeException("Lead stage with position '" + leadStageDto.getPosition() + "' already exists");
        }
        
        LeadStage leadStage = leadStageMapper.toEntity(leadStageDto);
        LeadStage savedLeadStage = leadStageRepository.save(leadStage);
        
        log.info("Lead stage created successfully with ID: {}", savedLeadStage.getId());
        return leadStageMapper.toDto(savedLeadStage);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadStageDto getLeadStageById(Long id) {
        log.info("Getting lead stage by ID: {}", id);
        
        LeadStage leadStage = leadStageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead stage not found with ID: " + id));
        
        return leadStageMapper.toDto(leadStage);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadStageDto getLeadStageByName(String name) {
        log.info("Getting lead stage by name: {}", name);
        
        LeadStage leadStage = leadStageRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Lead stage not found with name: " + name));
        
        return leadStageMapper.toDto(leadStage);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadStageDto getLeadStageByPosition(Integer position) {
        log.info("Getting lead stage by position: {}", position);
        
        LeadStage leadStage = leadStageRepository.findByPosition(position)
                .orElseThrow(() -> new RuntimeException("Lead stage not found with position: " + position));
        
        return leadStageMapper.toDto(leadStage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadStageDto> getAllLeadStages() {
        log.info("Getting all lead stages");
        
        List<LeadStage> leadStages = leadStageRepository.findAllByOrderByPositionAsc();
        return leadStageMapper.toDtoList(leadStages);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeadStageDto> getAllLeadStages(Pageable pageable) {
        log.info("Getting all lead stages with pagination: {}", pageable);
        
        Page<LeadStage> leadStages = leadStageRepository.findAllByOrderByPositionAsc(pageable);
        return leadStages.map(leadStageMapper::toDto);
    }

    @Override
    public LeadStageDto updateLeadStage(Long id, LeadStageDto leadStageDto) {
        log.info("Updating lead stage with ID: {}", id);
        
        LeadStage existingLeadStage = leadStageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead stage not found with ID: " + id));
        
        // Check if the new name conflicts with another lead stage
        if (!existingLeadStage.getName().equalsIgnoreCase(leadStageDto.getName()) &&
            leadStageRepository.existsByNameIgnoreCase(leadStageDto.getName())) {
            throw new RuntimeException("Lead stage with name '" + leadStageDto.getName() + "' already exists");
        }
        
        // Check if the new position conflicts with another lead stage
        if (!existingLeadStage.getPosition().equals(leadStageDto.getPosition()) &&
            leadStageRepository.findByPosition(leadStageDto.getPosition()).isPresent()) {
            throw new RuntimeException("Lead stage with position '" + leadStageDto.getPosition() + "' already exists");
        }
        
        // Update fields
        existingLeadStage.setName(leadStageDto.getName());
        existingLeadStage.setPosition(leadStageDto.getPosition());
        
        LeadStage updatedLeadStage = leadStageRepository.save(existingLeadStage);
        log.info("Lead stage updated successfully with ID: {}", updatedLeadStage.getId());
        
        return leadStageMapper.toDto(updatedLeadStage);
    }

    @Override
    public void deleteLeadStage(Long id) {
        log.info("Deleting lead stage with ID: {}", id);
        
        LeadStage leadStage = leadStageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead stage not found with ID: " + id));
        
        // Check if lead stage is being used by any leads
        // This would require a join query or additional repository method
        // For now, we'll allow deletion and handle foreign key constraints at database level
        
        leadStageRepository.deleteById(id);
        log.info("Lead stage deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadStageDto getFirstStage() {
        log.info("Getting first lead stage");
        
        LeadStage firstStage = leadStageRepository.findFirstByOrderByPositionAsc()
                .orElseThrow(() -> new RuntimeException("No lead stages found"));
        
        return leadStageMapper.toDto(firstStage);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadStageDto getLastStage() {
        log.info("Getting last lead stage");
        
        LeadStage lastStage = leadStageRepository.findFirstByOrderByPositionDesc()
                .orElseThrow(() -> new RuntimeException("No lead stages found"));
        
        return leadStageMapper.toDto(lastStage);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadStageDto getNextStage(Integer currentPosition) {
        log.info("Getting next lead stage from position: {}", currentPosition);
        
        List<LeadStage> nextStages = leadStageRepository.findByPositionGreaterThan(currentPosition);
        
        if (nextStages.isEmpty()) {
            throw new RuntimeException("No next stage found for position: " + currentPosition);
        }
        
        // Get the stage with the lowest position greater than current
        LeadStage nextStage = nextStages.stream()
                .min((s1, s2) -> Integer.compare(s1.getPosition(), s2.getPosition()))
                .orElse(null);
        
        return leadStageMapper.toDto(nextStage);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadStageDto getPreviousStage(Integer currentPosition) {
        log.info("Getting previous lead stage from position: {}", currentPosition);
        
        List<LeadStage> previousStages = leadStageRepository.findByPositionLessThan(currentPosition);
        
        if (previousStages.isEmpty()) {
            throw new RuntimeException("No previous stage found for position: " + currentPosition);
        }
        
        // Get the stage with the highest position less than current
        LeadStage previousStage = previousStages.stream()
                .max((s1, s2) -> Integer.compare(s1.getPosition(), s2.getPosition()))
                .orElse(null);
        
        return leadStageMapper.toDto(previousStage);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        log.info("Checking if lead stage exists by name: {}", name);
        
        return leadStageRepository.existsByNameIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPosition(Integer position) {
        log.info("Checking if lead stage exists by position: {}", position);

        return leadStageRepository.findByPosition(position).orElseThrow() != null;
    }
} 