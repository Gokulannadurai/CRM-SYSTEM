package com.crm.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CRM User Service Main Application Class
 * 
 * This is the main entry point for the CRM User microservice.
 * It provides user management and authentication functionality for the CRM system.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@SpringBootApplication
public class UserServiceApplication {

    /**
     * Main method to start the User Service
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
} 