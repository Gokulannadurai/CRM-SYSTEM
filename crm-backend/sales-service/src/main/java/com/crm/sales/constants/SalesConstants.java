package com.crm.sales.constants;

/**
 * Sales Service Constants
 * 
 * This class contains all the constants used throughout the sales service
 * including lead statuses, opportunity stages, deal statuses, and other
 * sales-related constants.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
public final class SalesConstants {

    /**
     * Private constructor to prevent instantiation
     */
    private SalesConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Lead Status Constants
    public static final String LEAD_STATUS_NEW = "NEW";
    public static final String LEAD_STATUS_CONTACTED = "CONTACTED";
    public static final String LEAD_STATUS_QUALIFIED = "QUALIFIED";
    public static final String LEAD_STATUS_DISQUALIFIED = "DISQUALIFIED";
    public static final String LEAD_STATUS_CONVERTED = "CONVERTED";

    public static final String UNDER_SCORE = "_";
    public static final String TIMEZONE_UTC = "UTC";
    public static final String HTTPS = "https";
    public static final String COLON = ":";
    public static final String SPACE = " ";
    public static final String EMPTY = "";
    public static final String FORWARD_SLASH = "/";
    public static final String S3_HOST_NAME = SalesConstants.HTTPS
            + SalesConstants.COLON + SalesConstants.FORWARD_SLASH + SalesConstants.FORWARD_SLASH
            + "%s.s3.%s" + ".amazonaws.com/";

    // Opportunity Stage Constants
    public static final String OPPORTUNITY_STAGE_PROSPECTING = "PROSPECTING";
    public static final String OPPORTUNITY_STAGE_QUALIFICATION = "QUALIFICATION";
    public static final String OPPORTUNITY_STAGE_PROPOSAL = "PROPOSAL";
    public static final String OPPORTUNITY_STAGE_NEGOTIATION = "NEGOTIATION";
    public static final String OPPORTUNITY_STAGE_CLOSED_WON = "CLOSED_WON";
    public static final String OPPORTUNITY_STAGE_CLOSED_LOST = "CLOSED_LOST";

    // Deal Status Constants
    public static final String DEAL_STATUS_OPEN = "OPEN";
    public static final String DEAL_STATUS_WON = "WON";
    public static final String DEAL_STATUS_LOST = "LOST";
    public static final String DEAL_STATUS_CANCELLED = "CANCELLED";

    // Lead Source Constants
    public static final String LEAD_SOURCE_WEBSITE = "WEBSITE";
    public static final String LEAD_SOURCE_REFERRAL = "REFERRAL";
    public static final String LEAD_SOURCE_SOCIAL_MEDIA = "SOCIAL_MEDIA";
    public static final String LEAD_SOURCE_EMAIL_CAMPAIGN = "EMAIL_CAMPAIGN";
    public static final String LEAD_SOURCE_COLD_CALL = "COLD_CALL";
    public static final String LEAD_SOURCE_TRADE_SHOW = "TRADE_SHOW";
    public static final String LEAD_SOURCE_OTHER = "OTHER";

    // Priority Constants
    public static final String PRIORITY_LOW = "LOW";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_URGENT = "URGENT";

    // Currency Constants
    public static final String CURRENCY_USD = "USD";
    public static final String CURRENCY_EUR = "EUR";
    public static final String CURRENCY_GBP = "GBP";
    public static final String CURRENCY_INR = "INR";

    // Probability Constants
    public static final int PROBABILITY_MIN = 0;
    public static final int PROBABILITY_MAX = 100;
    public static final int PROBABILITY_DEFAULT = 0;

    // Validation Constants
    public static final int MAX_LEAD_NAME_LENGTH = 255;
    public static final int MAX_OPPORTUNITY_NAME_LENGTH = 255;
    public static final int MAX_DEAL_NAME_LENGTH = 255;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    public static final int MAX_NOTES_LENGTH = 2000;

    // Pagination Constants
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // Cache Constants
    public static final String CACHE_LEADS = "leads";
    public static final String CACHE_OPPORTUNITIES = "opportunities";
    public static final String CACHE_DEALS = "deals";
    public static final String CACHE_PIPELINE_STAGES = "pipelineStages";
    public static final String CACHE_LEAD_SOURCES = "leadSources";

    // Error Messages
    public static final String ERROR_LEAD_NOT_FOUND = "Lead not found with id: ";
    public static final String ERROR_OPPORTUNITY_NOT_FOUND = "Opportunity not found with id: ";
    public static final String ERROR_DEAL_NOT_FOUND = "Deal not found with id: ";
    public static final String ERROR_INVALID_PROBABILITY = "Probability must be between 0 and 100";
    public static final String ERROR_INVALID_DEAL_VALUE = "Deal value must be greater than 0";
    public static final String ERROR_INVALID_CLOSE_DATE = "Close date cannot be in the past";
    public static final String ERROR_LEAD_ALREADY_CONVERTED = "Lead has already been converted to opportunity";

    // Success Messages
    public static final String SUCCESS_LEAD_CREATED = "Lead created successfully";
    public static final String SUCCESS_LEAD_UPDATED = "Lead updated successfully";
    public static final String SUCCESS_LEAD_DELETED = "Lead deleted successfully";
    public static final String SUCCESS_OPPORTUNITY_CREATED = "Opportunity created successfully";
    public static final String SUCCESS_OPPORTUNITY_UPDATED = "Opportunity updated successfully";
    public static final String SUCCESS_OPPORTUNITY_DELETED = "Opportunity deleted successfully";
    public static final String SUCCESS_DEAL_CREATED = "Deal created successfully";
    public static final String SUCCESS_DEAL_UPDATED = "Deal updated successfully";
    public static final String SUCCESS_DEAL_DELETED = "Deal deleted successfully";
    public static final String SUCCESS_LEAD_CONVERTED = "Lead converted to opportunity successfully";

    // API Endpoints
    public static final String API_BASE_PATH = "/api/v1/sales";
    public static final String API_LEADS = "/leads";
    public static final String API_OPPORTUNITIES = "/opportunities";
    public static final String API_DEALS = "/deals";
    public static final String API_PIPELINE = "/pipeline";
    public static final String API_ANALYTICS = "/analytics";

    // Date Format Constants
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // File Export Constants
    public static final String EXPORT_CSV = "CSV";
    public static final String EXPORT_PDF = "PDF";
    public static final String EXPORT_EXCEL = "EXCEL";

    // Notification Constants
    public static final String NOTIFICATION_LEAD_ASSIGNED = "LEAD_ASSIGNED";
    public static final String NOTIFICATION_OPPORTUNITY_STAGE_CHANGED = "OPPORTUNITY_STAGE_CHANGED";
    public static final String NOTIFICATION_DEAL_CLOSED = "DEAL_CLOSED";
    public static final String NOTIFICATION_DEAL_LOST = "DEAL_LOST";
} 