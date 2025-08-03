package com.crm.auth.config;

import com.crm.auth.client.UserServiceClient;
import com.crm.auth.filter.JwtAuthenticationFilter;
import com.crm.auth.service.JwtBasedUserDetailsService;
import com.crm.auth.service.JwtService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Shared Authentication Auto-Configuration
 * 
 * Automatically configures shared authentication components when included in a microservice.
 * 
 * @version 1.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Configuration
@EnableFeignClients(basePackageClasses = UserServiceClient.class)
@ComponentScan(basePackages = "com.crm.auth")
@ConditionalOnClass(JwtAuthenticationFilter.class)
public class SharedAuthAutoConfiguration {

    /**
     * Configure JWT Service
     * 
     * @return JWT service bean
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtService jwtService() {
        return new JwtService();
    }

    /**
     * Configure JWT-based User Details Service (Primary)
     * 
     * This service creates UserDetails from JWT token claims without requiring
     * external service calls, eliminating circular dependency issues.
     * 
     * @param jwtService the JWT service
     * @return JWT-based user details service bean
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "jwtBasedUserDetailsService")
    public UserDetailsService jwtBasedUserDetailsService(JwtService jwtService) {
        return new JwtBasedUserDetailsService(jwtService);
    }

    /**
     * Configure JWT Authentication Filter
     * 
     * @param jwtService the JWT service
     * @param userDetailsService the user details service (will use JWT-based by default)
     * @return JWT authentication filter bean
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }
} 