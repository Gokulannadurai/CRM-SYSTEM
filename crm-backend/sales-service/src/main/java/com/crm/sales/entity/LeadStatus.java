package com.crm.sales.entity;

/**
 * Lead Status Enumeration
 * 
 * This enum defines the possible statuses for a sales lead.
 * The status indicates the current stage of the lead in the sales process.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
public enum LeadStatus {

    /**
     * New lead that has just been created
     */
    NEW("New Lead"),

    /**
     * Lead has been contacted but not yet qualified
     */
    CONTACTED("Contacted"),

    /**
     * Lead has been qualified and shows potential
     */
    QUALIFIED("Qualified"),

    /**
     * Lead has been disqualified and is not a good fit
     */
    DISQUALIFIED("Disqualified"),

    /**
     * Lead has been converted to an opportunity
     */
    CONVERTED("Converted");

    private final String displayName;

    /**
     * Constructor for LeadStatus enum
     * 
     * @param displayName the display name for the status
     */
    LeadStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the display name for the status
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if the lead can be converted to an opportunity
     * 
     * @return true if the lead can be converted
     */
    public boolean canBeConverted() {
        return this == QUALIFIED;
    }

    /**
     * Check if the lead is active (not disqualified or converted)
     * 
     * @return true if the lead is active
     */
    public boolean isActive() {
        return this != DISQUALIFIED && this != CONVERTED;
    }

    /**
     * Get the next logical status in the lead progression
     * 
     * @return the next status, or null if no progression is possible
     */
    public LeadStatus getNextStatus() {
        return switch (this) {
            case NEW -> CONTACTED;
            case CONTACTED -> QUALIFIED;
            case QUALIFIED -> CONVERTED;
            case DISQUALIFIED, CONVERTED -> null;
        };
    }

    @Override
    public String toString() {
        return displayName;
    }
} 