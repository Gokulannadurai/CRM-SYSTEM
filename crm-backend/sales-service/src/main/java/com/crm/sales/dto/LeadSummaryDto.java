package com.crm.sales.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Lead Summary DTO
 * 
 * Simplified lead data for inclusion in task details.
 * Contains essential lead information without full details.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Data
public class LeadSummaryDto {
    private Long id;
    private String title;
    private Long customerId;
    private String customerName;
    private String status;
    private Date expectedCloseDate;
    private BigDecimal value;
    private Date createdAt;
} 