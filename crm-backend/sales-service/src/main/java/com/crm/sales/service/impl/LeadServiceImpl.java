package com.crm.sales.service.impl;

import com.crm.sales.common.AwsFileService;
import com.crm.sales.dto.LeadDto;
import com.crm.sales.entity.Attachment;
import com.crm.sales.entity.Lead;
import com.crm.sales.entity.LeadStage;
import com.crm.sales.entity.Task;
import com.crm.sales.mapper.AttachmentMapper;
import com.crm.sales.mapper.LeadMapper;
import com.crm.sales.repository.LeadRepository;
import com.crm.sales.repository.LeadStageRepository;
import com.crm.sales.repository.TaskRepository;
import com.crm.sales.service.LeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Lead Service Implementation
 * 
 * Implementation of LeadService interface with business logic for lead management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final LeadStageRepository leadStageRepository;
    private final TaskRepository taskRepository;
    private final LeadMapper leadMapper;
    private final AttachmentMapper attachmentMapper;
    private final AwsFileService awsFileService;

    @Override
    public LeadDto createOrUpdateLead(LeadDto leadDto, List<MultipartFile> files) {
        log.info("Creating/updating lead - ID: {}, Title: {}, CustomerId: {}, AdditionalNotes: {}", 
                leadDto.getId(), leadDto.getTitle(), leadDto.getCustomerId(), leadDto.getAdditionalNotes());
        List<Attachment> attachments = new ArrayList<>();
        int count = 1;
        Lead lead = null;
        if (Objects.nonNull(leadDto.getId())) {
            Optional<Lead> leadOptional
                    = leadRepository.findById(leadDto.getId());
            if (leadOptional.isPresent()) {
                lead = leadOptional.get();
                log.info("Found existing lead for update: {}", lead.getId());
               // status = CommunityPostEvent.COMMUNITY_POST_UPDATED.getEventMessage();
            } else {
                log.error("Lead not found for update with ID: {}", leadDto.getId());
                throw new RuntimeException("Lead not found with ID: " + leadDto.getId());
            }
        } else {
            lead = leadMapper.toEntity(leadDto);
            lead.setStatus("Lead");
            log.info("Creating new lead entity");
        }
        
        // Set customer name and user name from DTO
        lead.setCustomerName(leadDto.getCustomerName());
        lead.setUserName(leadDto.getUserName());
        
        if (files != null && !files.isEmpty()) {
            for (MultipartFile imageFile : files) {
                String imageUrl = awsFileService.uploadFile(imageFile);
                attachments.add(attachmentMapper.converToAttachment(imageUrl, count,
                        imageFile.getOriginalFilename()));
                count++;
            }
        }
        lead.setAttachments(attachments);
        Lead savedLead = leadRepository.save(lead);
        log.info("Lead created successfully with ID: {}", savedLead.getId());
        return leadMapper.toDto(savedLead);
    }

    @Override
    @Transactional(readOnly = true)
    public LeadDto getLeadById(Long id) {
        log.info("Getting lead by ID: {}", id);
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + id));
        
        // Fetch related tasks for this lead
        List<Task> relatedTasks = taskRepository.findByLeadId(id);
        log.info("Found {} related tasks for lead ID: {}", relatedTasks.size(), id);
        
        // Use mapper with tasks
        LeadDto leadDto = leadMapper.toDtoWithTasks(lead, relatedTasks);
        return awsFileService.setAttachmentsToLead(lead, leadDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeadDto> getAllLeads(Pageable pageable) {
        log.info("Getting all leads with pagination: {}", pageable);
        
        Page<Lead> leads = leadRepository.findAll(pageable);
        return leads.map(leadMapper::toDto);
    }

    @Override
    public void deleteLead(Long id) {
        log.info("Deleting lead with ID: {}", id);
        
        if (!leadRepository.existsById(id)) {
            throw new RuntimeException("Lead not found with ID: " + id);
        }
        
        leadRepository.deleteById(id);
        log.info("Lead deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDto> getLeadsByCustomerId(Long customerId) {
        log.info("Getting leads for customer: {}", customerId);
        
        List<Lead> leads = leadRepository.findByCustomerId(customerId);
        return leadMapper.toDtoList(leads);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDto> getLeadsByAssignedTo(Long assignedTo) {
        log.info("Getting leads assigned to user: {}", assignedTo);
        
        List<Lead> leads = leadRepository.findByAssignedTo(assignedTo);
        return leadMapper.toDtoList(leads);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDto> getLeadsByStatus(String status) {
        log.info("Getting leads by status: {}", status);
        
        List<Lead> leads = leadRepository.findByStatus(status);
        return leadMapper.toDtoList(leads);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDto> getLeadsByCurrentStage(Long currentStageId) {
        log.info("Getting leads by current stage: {}", currentStageId);
        
        List<Lead> leads = leadRepository.findByCurrentStageId(currentStageId);
        return leadMapper.toDtoList(leads);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDto> getOverdueLeads() {
        log.info("Getting overdue leads");
        
        List<Lead> leads = leadRepository.findOverdueLeads(new Date());
        return leadMapper.toDtoList(leads);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDto> getLeadsDueSoon(int days) {
        log.info("Getting leads due soon within {} days", days);
        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        Date daysFromNow = calendar.getTime();
        
        List<Lead> leads = leadRepository.findLeadsDueSoon(now, daysFromNow);
        return leadMapper.toDtoList(leads);
    }

    @Override
    public LeadDto moveToNextStage(Long id) {
        log.info("Moving lead {} to next stage", id);
        
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + id));
        
        if (lead.getCurrentStageId() == null) {
            // If no current stage, move to first stage
            LeadStage firstStage = leadStageRepository.findAllByOrderByPositionAsc().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No lead stages found"));
            lead.setCurrentStageId(firstStage.getId());
        } else {
            // Find current stage and move to next
            LeadStage currentStage = leadStageRepository.findById(lead.getCurrentStageId())
                    .orElseThrow(() -> new RuntimeException("Current stage not found"));
            
            LeadStage nextStage = leadStageRepository.findAllByOrderByPositionAsc().stream()
                    .filter(stage -> stage.getPosition() > currentStage.getPosition())
                    .findFirst()
                    .orElse(null);
            
            if (nextStage != null) {
                lead.setCurrentStageId(nextStage.getId());
                lead.setStatus(nextStage.getName());
            }
        }
        
        Lead updatedLead = leadRepository.save(lead);
        log.info("Lead {} moved to next stage: {}", id, updatedLead.getCurrentStageId());
        
        return leadMapper.toDto(updatedLead);
    }

    @Override
    public LeadDto moveToPreviousStage(Long id) {
        log.info("Moving lead {} to previous stage", id);
        
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + id));
        
        if (lead.getCurrentStageId() == null) {
            throw new RuntimeException("Lead has no current stage to move from");
        }
        
        // Find current stage and move to previous
        LeadStage currentStage = leadStageRepository.findById(lead.getCurrentStageId())
                .orElseThrow(() -> new RuntimeException("Current stage not found"));
        
        LeadStage previousStage = leadStageRepository.findAllByOrderByPositionAsc().stream()
                .filter(stage -> stage.getPosition() < currentStage.getPosition())
                .reduce((first, second) -> second) // Get the last one (highest position < current)
                .orElse(null);
        
        if (previousStage != null) {
            lead.setCurrentStageId(previousStage.getId());
            lead.setStatus(previousStage.getName());
        }
        
        Lead updatedLead = leadRepository.save(lead);
        log.info("Lead {} moved to previous stage: {}", id, updatedLead.getCurrentStageId());
        
        return leadMapper.toDto(updatedLead);
    }

    @Override
    public LeadDto assignLead(Long id, Long assignedTo) {
        log.info("Assigning lead {} to user {}", id, assignedTo);
        
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + id));
        
        lead.setAssignedTo(assignedTo);
        Lead updatedLead = leadRepository.save(lead);
        
        log.info("Lead {} assigned to user {}", id, assignedTo);
        return leadMapper.toDto(updatedLead);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getLeadStatistics() {
        log.info("Getting lead statistics");
        
        Map<String, Object> statistics = new HashMap<>();
        
        // Total leads
        long totalLeads = leadRepository.count();
        statistics.put("totalLeads", totalLeads);
        
        // Leads by status
        long activeLeads = leadRepository.countByStatus("Lead");
        statistics.put("activeLeads", activeLeads);
        
        // Leads by assigned user
        long assignedLeads = leadRepository.countByAssignedTo(1L); // Example user ID
        statistics.put("assignedLeads", assignedLeads);

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        Date after7Days = calendar.getTime();

        // Overdue leads
        List<Lead> overdueLeads = leadRepository.findOverdueLeads(now);
        statistics.put("overdueLeads", overdueLeads.size());
        
        // Leads due soon
        List<Lead> leadsDueSoon = leadRepository.findLeadsDueSoon(now, after7Days);
        statistics.put("leadsDueSoon", leadsDueSoon.size());
        
        log.info("Lead statistics generated: {}", statistics);
        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getPipelineStatistics() {
        log.info("Getting pipeline statistics");
        
        Map<String, Object> statistics = new HashMap<>();
        
        // Get all stages
        List<LeadStage> stages = leadStageRepository.findAllByOrderByPositionAsc();
        
        // Count leads in each stage
        for (LeadStage stage : stages) {
            long count = leadRepository.countByCurrentStageId(stage.getId());
            statistics.put(stage.getName(), count);
        }
        
        // Total pipeline value
        double totalValue = leadRepository.findAll().stream()
                .mapToDouble(lead -> lead.getValue() != null ? lead.getValue().doubleValue() : 0.0)
                .sum();
        statistics.put("totalPipelineValue", totalValue);
        
        log.info("Pipeline statistics generated: {}", statistics);
        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeadDto> getAllLeadsForDropdown() {
        log.debug("Getting all leads for dropdown");
        List<Lead> leads = leadRepository.findAll();
        return leads.stream()
                .map(leadMapper::toDto)
                .collect(Collectors.toList());
    }
} 