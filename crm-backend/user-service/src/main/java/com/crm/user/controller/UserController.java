package com.crm.user.controller;

import com.crm.user.dto.ForgotPasswordRequest;
import com.crm.user.dto.ForgotPasswordResponse;
import com.crm.user.dto.LoginRequest;
import com.crm.user.dto.LoginResponse;
import com.crm.user.dto.UserDto;
import com.crm.user.dto.TokenValidationResponse;
import com.crm.user.entity.User;
import com.crm.user.entity.Role;
import com.crm.user.mapper.UserMapper;
import com.crm.user.service.JwtService;
import com.crm.user.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Controller
 *
 * REST controller for user management and authentication operations.
 *
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userServiceImpl;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    /**
     * Constructor with dependencies
     *
     * @param authenticationManager the authentication manager
     * @param userServiceImpl the user service
     * @param jwtService the JWT service
     */
    @Autowired
    public UserController(AuthenticationManager authenticationManager,
                         UserServiceImpl userServiceImpl,
                         JwtService jwtService, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userServiceImpl = userServiceImpl;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    /**
     * User login endpoint
     *
     * @param loginRequest the login request
     * @param request the HTTP request
     * @return login response with tokens
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                              HttpServletRequest request) {
        logger.info("Login attempt for user: {}", loginRequest.getUsername());

        try {
            // Validate credentials
            Optional<User> userOpt = userServiceImpl.validateCredentials(
                loginRequest.getUsername(), loginRequest.getPassword());

            if (userOpt.isEmpty()) {
                logger.warn("Login failed for user: {}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid username or password"));
            }

            User user = userOpt.get();

            // Check if user is active
            if (!user.isActive()) {
                logger.warn("Login attempt for inactive user: {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("User account is inactive"));
            }

            // Generate tokens
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // Update last login time
            userServiceImpl.updateLastLoginTime(user.getId());

            LoginResponse response = new LoginResponse(
                accessToken,
                refreshToken,
                jwtService.extractExpiration(accessToken).getTime(),
                user.getEmail(),
                user.getRoles().stream()
                    .map(Role::getName)             // Extract the role name
                    .collect(Collectors.joining(", ")),
                "Login successful"
            );

            logger.info("Login successful for user: {}", user.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Login error for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Login failed: " + e.getMessage()));
        }
    }

    /**
     * Refresh token endpoint
     *
     * @param refreshToken the refresh token
     * @param request the HTTP request
     * @return new access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader("X-Refresh-Token") String refreshToken,
                                                           HttpServletRequest request) {
        logger.info("Token refresh request");

        try {
            // Validate refresh token
            if (!jwtService.validateToken(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
                logger.warn("Invalid refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorMap("Invalid refresh token"));
            }
            
            // Extract user information
            String username = jwtService.extractUsername(refreshToken);
            Long userId = jwtService.extractUserId(refreshToken);
            
            // Get user
            Optional<User> userOpt = userServiceImpl.getUserById(userId);
            if (userOpt.isEmpty() || !userOpt.get().isActive()) {
                logger.warn("User not found or inactive for refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorMap("User not found or inactive"));
            }
            
            User user = userOpt.get();
            
            // Generate new access token
            String newAccessToken = jwtService.generateToken(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("access_token", newAccessToken);
            response.put("token_type", "Bearer");
            response.put("expires_in", jwtService.extractExpiration(newAccessToken).getTime());
            response.put("message", "Token refreshed successfully");
            
            logger.info("Token refreshed successfully for user: {}", user.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Token refresh error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorMap("Token refresh failed: " + e.getMessage()));
        }
    }

    /**
     * Validate JWT token endpoint
     * 
     * @param authorization the JWT token from Authorization header
     * @return token validation response
     */
    @PostMapping("/validate-token")
    public ResponseEntity<TokenValidationResponse> validateToken(
            @RequestHeader("Authorization") String authorization) {
        logger.info("Token validation request");
        
        try {
            // Extract token from Authorization header (remove "Bearer " prefix)
            String token = authorization;
            if (authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
            }
            
            // Check if token is expired first
            if (jwtService.isTokenExpired(token)) {
                logger.warn("Token has expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(TokenValidationResponse.builder()
                        .valid(false)
                        .message("Token has expired. Please login again.")
                        .build());
            }
            
            // Validate token
            if (!jwtService.validateToken(token)) {
                logger.warn("Invalid token provided for validation");
                return ResponseEntity.ok(TokenValidationResponse.builder()
                    .valid(false)
                    .message("Invalid token")
                    .build());
            }
            
            // Extract user information
            String username = jwtService.extractUsername(token);
            Long userId = jwtService.extractUserId(token);
            
            // Get user details
            Optional<User> userOpt = userServiceImpl.getUserByUsername(username);
            if (userOpt.isEmpty() || !userOpt.get().isActive()) {
                logger.warn("User not found or inactive for token validation");
                return ResponseEntity.ok(TokenValidationResponse.builder()
                    .valid(false)
                    .message("User not found or inactive")
                    .build());
            }
            
            User user = userOpt.get();
            
            // Build response
            TokenValidationResponse response = TokenValidationResponse.builder()
                .valid(true)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", ")))
                .message("Token is valid")
                .expiresAt(jwtService.extractExpiration(token).getTime())
                .build();
            
            logger.info("Token validation successful for user: {}", user.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Token validation error", e);
            return ResponseEntity.ok(TokenValidationResponse.builder()
                .valid(false)
                .message("Token validation failed: " + e.getMessage())
                .build());
        }
    }

    /**
     * Get all users with pagination
     * 
     * @param page page number
     * @param size page size
     * @param authentication the authentication object
     * @return page of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES_MANAGER')")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String searchTerm,
            Authentication authentication) {
        
        logger.info("Getting users - page: {}, size: {}, isActive: {}, role: {}, searchTerm: {}", page, size, isActive, role, searchTerm);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> users;
            
            // If search term is provided, use search functionality
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                users = userServiceImpl.searchUsers(searchTerm.trim(), pageable);
            } else {
                // Filter by active status if provided
                if (isActive != null) {
                    users = userServiceImpl.getUsersByActiveStatus(isActive, pageable);
                } else {
                    users = userServiceImpl.getAllUsers(pageable);
                }
            }
            
            Page<UserDto> userDtos = users.map(userMapper::toDto);
            
            // Filter by role if provided - if role is null or empty, return all users without role filtering
            if (role != null && !role.trim().isEmpty()) {
                List<UserDto> filteredList = userDtos.getContent().stream()
                        .filter(userDto -> {
                            if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
                                return userDto.getRoles().stream()
                                        .anyMatch(r -> r.getName().equalsIgnoreCase(role.trim()));
                            }
                            return false;
                        })
                        .collect(Collectors.toList());

                userDtos = new PageImpl<>(
                        filteredList,
                        PageRequest.of(userDtos.getNumber(), userDtos.getSize(), userDtos.getSort()),
                        filteredList.size()
                );
            }
            
            return ResponseEntity.ok(userDtos);
            
        } catch (Exception e) {
            logger.error("Error getting users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get user by ID
     * 
     * @param userId the user ID
     * @param authentication the authentication object
     * @return the user
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId,
                                              Authentication authentication) {
        logger.info("Getting user by ID: {}", userId);
        
        try {
            Optional<User> userOpt = userServiceImpl.getUserById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            UserDto userDto = userMapper.toDto(userOpt.get());
            return ResponseEntity.ok(userDto);
            
        } catch (Exception e) {
            logger.error("Error getting user by ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create new user
     * 
     * @param userDto the user to create
     * @param authentication the authentication object
     * @return the created user
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto,
                                             Authentication authentication) {
        logger.info("Creating new user: {}", userDto.getUsername());
        
        try {
            User user = userMapper.toEntity(userDto);
            user = userServiceImpl.createUser(user);
            userDto = userMapper.toDto(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
        } catch (RuntimeException e) {
            logger.warn("User creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update user
     * 
     * @param userId the user ID
     * @param userDto the updated user data
     * @param authentication the authentication object
     * @return the updated user
     */
    @PostMapping("/{userId}/update")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId,
                                             @Valid @RequestBody UserDto userDto,
                                             Authentication authentication) {
        logger.info("Updating user: {}", userId);
        
        try {
            // Validate userDto before mapping
            if (userDto == null) {
                logger.warn("User update failed: Request body is null");
                return ResponseEntity.badRequest().build();
            }
            
            User user = userMapper.toEntity(userDto);
            if (user == null) {
                logger.warn("User update failed: Failed to map UserDto to User entity");
                return ResponseEntity.badRequest().build();
            }
            
            User updatedUser = userServiceImpl.updateUser(userId, user);
            UserDto updatedUserDto = userMapper.toDto(updatedUser);
            
            return ResponseEntity.ok(updatedUserDto);
            
        } catch (RuntimeException e) {
            logger.warn("User update failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete user
     * 
     * @param userId the user ID
     * @param authentication the authentication object
     * @return no content response
     */
    @PostMapping("/{userId}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId,
                                          Authentication authentication) {
        logger.info("Deleting user: {}", userId);
        
        try {
            // Validate userId
            if (userId == null || userId <= 0) {
                logger.warn("User deletion failed: Invalid user ID: {}", userId);
                return ResponseEntity.badRequest().build();
            }
            
            userServiceImpl.deleteUser(userId);
            return ResponseEntity.noContent().build();
            
        } catch (RuntimeException e) {
            logger.warn("User deletion failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error deleting user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search users
     * 
     * @param searchTerm the search term
     * @param page page number
     * @param size page size
     * @param authentication the authentication object
     * @return page of matching users
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SALES_MANAGER')")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        logger.info("Searching users with term: {}", searchTerm);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> users = userServiceImpl.searchUsers(searchTerm, pageable);
            Page<UserDto> userDtos = users.map(userMapper::toDto);
            return ResponseEntity.ok(userDtos);
        } catch (Exception e) {
            logger.error("Error searching users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Forgot password endpoint
     * 
     * @param forgotPasswordRequest the forgot password request
     * @return forgot password response
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        logger.info("Forgot password request for email: {}", forgotPasswordRequest.getEmail());
        
        try {
            ForgotPasswordResponse response = userServiceImpl.processForgotPassword(forgotPasswordRequest.getEmail());
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            logger.error("Error processing forgot password request for email: {}", forgotPasswordRequest.getEmail(), e);
            ForgotPasswordResponse errorResponse = ForgotPasswordResponse.builder()
                    .success(false)
                    .message("An error occurred while processing your request. Please try again later.")
                    .email(forgotPasswordRequest.getEmail())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get user by username endpoint
     * 
     * @param username the username
     * @return the user details
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        logger.info("Getting user by username: {}", username);
        
        try {
            Optional<User> userOpt = userServiceImpl.getUserByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            UserDto userDto = userMapper.toDto(userOpt.get());
            return ResponseEntity.ok(userDto);
            
        } catch (Exception e) {
            logger.error("Error getting user by username: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all users for dropdown (id and name only)
     * 
     * @return list of users with id and name
     */
    @GetMapping("/dropdown")
    public ResponseEntity<Map<String, Object>> getAllUsersForDropdown() {
        logger.info("Getting all users for dropdown");
        
        try {
            List<Map<String, Object>> users = userServiceImpl.getAllActiveUsers().stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("name", user.getFirstName() + " " + user.getLastName());
                    return userMap;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", users);
            response.put("message", "Users retrieved successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting users for dropdown", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to retrieve users");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Health check endpoint
     * 
     * @return health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "User Service");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Create error response for login
     * 
     * @param errorMessage the error message
     * @return error response
     */
    private LoginResponse createErrorResponse(String errorMessage) {
        return new LoginResponse(null, null, null, null, null,errorMessage);
    }

    /**
     * Create error map
     * 
     * @param errorMessage the error message
     * @return error map
     */
    private Map<String, Object> createErrorMap(String errorMessage) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("error", errorMessage);
        errorMap.put("timestamp", System.currentTimeMillis());
        return errorMap;
    }

    /**
     * Handle validation exceptions
     * 
     * @param ex the validation exception
     * @return error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");
        response.put("details", errors);
        response.put("timestamp", System.currentTimeMillis());
        
        logger.warn("Validation failed: {}", errors);
        return ResponseEntity.badRequest().body(response);
    }
} 