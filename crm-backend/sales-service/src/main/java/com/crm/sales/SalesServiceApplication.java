package com.crm.sales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Sales Service Application
 * 
 * Main application class for the Sales Service.
 * Provides sales pipeline management, lead tracking, and task management.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@SpringBootApplication(scanBasePackages = {"com.crm.sales", "com.crm.auth"})
@EnableJpaAuditing
@EnableFeignClients
public class SalesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalesServiceApplication.class, args);
    }
} 