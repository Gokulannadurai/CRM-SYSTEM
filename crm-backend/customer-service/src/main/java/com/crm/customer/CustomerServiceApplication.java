package com.crm.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Customer Service Application
 * 
 * Main application class for Customer Management Service.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@SpringBootApplication(scanBasePackages = {"com.crm.customer", "com.crm.auth"})
@EnableFeignClients
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
} 