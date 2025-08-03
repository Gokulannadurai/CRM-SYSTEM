package com.crm.customer.config;

import com.crm.auth.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Authentication Bean Configuration for Customer Service
 * 
 * Explicitly defines authentication beans to ensure they are available.
 * Uses JWT-based UserDetailsService from shared auth auto-configuration.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Configuration
public class AuthBeanConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(com.crm.auth.service.JwtService jwtService, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }
} 