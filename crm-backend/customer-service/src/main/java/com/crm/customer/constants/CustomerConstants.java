package com.crm.customer.constants;

/**
 * Customer Constants
 * 
 * Constants used throughout the customer service.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
public final class CustomerConstants {

    // API Paths
    public static final String API_BASE_PATH = "/api/v1";
    public static final String CUSTOMER_PATH = "/customers";
    public static final String INTERACTION_HISTORY_PATH = "/interaction-history";

    // Table Names
    public static final String TABLE_CUSTOMER = "customer";
    public static final String TABLE_INTERACTION_HISTORY = "interaction_history";

    // Column Names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_COMPANY = "company";
    public static final String COLUMN_SOURCE = "source";
    public static final String COLUMN_CREATED_BY = "created_by";
    public static final String COLUMN_IS_ACTIVE = "is_active";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    // Interaction History Column Names
    public static final String COLUMN_CUSTOMER_ID = "customer_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_INTERACTION_TYPE = "interaction_type";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_INTERACTION_DATE = "interaction_date";

    // Validation Constants
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 255;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MAX_PHONE_LENGTH = 20;
    public static final int MAX_COMPANY_LENGTH = 255;
    public static final int MAX_SOURCE_LENGTH = 100;
    public static final int MAX_INTERACTION_TYPE_LENGTH = 50;

    // Pagination Constants
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    // Error Messages
    public static final String ERROR_CUSTOMER_NOT_FOUND = "Customer not found";
    public static final String ERROR_EMAIL_ALREADY_EXISTS = "Customer with this email already exists";
    public static final String ERROR_INVALID_EMAIL = "Invalid email format";
    public static final String ERROR_INVALID_PHONE = "Invalid phone number format";
    public static final String ERROR_NAME_REQUIRED = "Name is required";
    public static final String ERROR_EMAIL_REQUIRED = "Email is required";

    // Interaction History Error Messages
    public static final String ERROR_INTERACTION_HISTORY_NOT_FOUND = "Interaction history not found";
    public static final String ERROR_CUSTOMER_ID_REQUIRED = "Customer ID is required";

    // Interaction Types
    public static final String INTERACTION_TYPE_CALL = "call";
    public static final String INTERACTION_TYPE_EMAIL = "email";
    public static final String INTERACTION_TYPE_MEETING = "meeting";
    public static final String INTERACTION_TYPE_NOTE = "note";
    public static final String INTERACTION_TYPE_TASK = "task";

    // Customer Sources
    public static final String SOURCE_REFERRAL = "Referral";
    public static final String SOURCE_WEBSITE = "Website";
    public static final String SOURCE_SOCIAL_MEDIA = "Social Media";
    public static final String SOURCE_COLD_CALL = "Cold Call";
    public static final String SOURCE_EVENT = "Event";
    public static final String SOURCE_OTHER = "Other";

    // Cache Names
    public static final String CACHE_CUSTOMER_BY_ID = "customerById";
    public static final String CACHE_CUSTOMER_BY_EMAIL = "customerByEmail";
    public static final String CACHE_INTERACTION_HISTORY_BY_CUSTOMER = "interactionHistoryByCustomer";

    // Security Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SALES_MANAGER = "SALES_MANAGER";
    public static final String ROLE_SALES_REPRESENTATIVE = "SALES_REPRESENTATIVE";

    // Private constructor to prevent instantiation
    private CustomerConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
} 