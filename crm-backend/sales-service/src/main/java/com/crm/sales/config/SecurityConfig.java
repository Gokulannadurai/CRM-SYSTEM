package com.crm.sales.config;

import com.crm.auth.filter.JwtAuthenticationFilter;
import com.crm.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security Configuration for Sales Service
 * 
 * Uses shared authentication library to eliminate duplicate authentication logic.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring SecurityFilterChain for Sales Service");
        
        http
            // Disable CSRF for stateless API
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configure authorization
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // Protected endpoints
                .requestMatchers("/api/v1/leads/**").hasAnyRole("ADMIN", "USER", "MANAGER", "SALES", "SALES_MANAGER", "SALES_REPRESENTATIVE")
                .requestMatchers("/api/v1/tasks/**").hasAnyRole("ADMIN", "USER", "MANAGER", "SALES", "SALES_MANAGER", "SALES_REPRESENTATIVE")
                .requestMatchers("/api/v1/stages/**").hasAnyRole("ADMIN", "USER", "MANAGER", "SALES", "SALES_MANAGER", "SALES_REPRESENTATIVE")
                .requestMatchers("/api/v1/task-types/**").hasAnyRole("ADMIN", "USER", "MANAGER", "SALES", "SALES_MANAGER", "SALES_REPRESENTATIVE")

                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Configure session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Add JWT authentication filter
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        logger.info("SecurityFilterChain configured successfully");
        return http.build();
    }

    /**
     * Configure CORS
     *
     * @return configured CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.debug("Configuring CORS");

        CorsConfiguration configuration = new CorsConfiguration();

        // Allow specific origins
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",  // React development server
                "http://localhost:3001",  // Alternative React port
                "https://crm-frontend.com", // Production frontend
                "https://*.crm-frontend.com" // Subdomains
        ));

        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "X-Refresh-Token"
        ));

        // Allow credentials
        configuration.setAllowCredentials(true);

        // Set max age for preflight requests
        configuration.setMaxAge(3600L);

        // Expose custom headers
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Refresh-Token"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        logger.debug("CORS configured successfully");
        return source;
    }
} 