package com.crm.user.service.impl;

import com.crm.user.config.EmailNotificationConfig;
import com.crm.user.dto.ForgotPasswordResponse;
import com.crm.user.entity.Role;
import com.crm.user.entity.User;
import com.crm.user.repository.RoleRepository;
import com.crm.user.repository.UserRepository;
import com.crm.user.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Service
 * 
 * Provides business logic for user management operations.
 * 
 * @version 1.0
 * @date December 2025
 * @author Gokul Annadurai
 */
@Service
@Transactional
public class UserServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailNotificationConfig emailNotificationConfig;

    /**
     * Constructor with dependencies
     * 
     * @param userRepository the user repository
     * @param passwordEncoder the password encoder
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder, RoleRepository roleRepository,
                           EmailNotificationConfig emailNotificationConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.emailNotificationConfig = emailNotificationConfig;
    }

    /**
     * Create a new user
     * 
     * @param user the user to create
     * @return the created user
     */
    public User createUser(User user) {
        logger.info("Creating new user: {}", user.getUsername());
        validateUserData(user);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        List<Long> roleIds = user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList());
        List<Role> roles = roleRepository.findAllById(roleIds);
        user.setRoles(new HashSet<>(roles));

        User savedUser = userRepository.save(user);
        logger.info("User created successfully: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Update an existing user
     * 
     * @param userId the user ID
     * @param updatedUser the updated user data
     * @return the updated user
     */
    public User updateUser(Long userId, User updatedUser) {
        logger.info("Updating user: {}", userId);
        
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        // Validate required fields for update
        if (updatedUser.getFirstName() == null || updatedUser.getFirstName().trim().isEmpty()) {
            throw new RuntimeException("First name is required");
        }
        if (updatedUser.getLastName() == null || updatedUser.getLastName().trim().isEmpty()) {
            throw new RuntimeException("Last name is required");
        }
        if (updatedUser.getEmail() == null || updatedUser.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        
        // Check if email is being changed and if it already exists
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) && 
            userRepository.existsByEmail(updatedUser.getEmail())) {
            throw new RuntimeException("Email already exists: " + updatedUser.getEmail());
        }
        
        // Update fields
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setMobileNumber(updatedUser.getMobileNumber());
        existingUser.setIsActive(updatedUser.getIsActive());
        existingUser.setUpdatedAt(new Date());
        
        // Update roles if provided
        if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
            List<Long> roleIds = updatedUser.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toList());
            List<Role> roles = roleRepository.findAllById(roleIds);
            existingUser.setRoles(new HashSet<>(roles));
        }
        
        // Update password if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        
        User savedUser = userRepository.save(existingUser);
        logger.info("User updated successfully: {}", savedUser.getId());
        
        return savedUser;
    }

    /**
     * Delete a user
     * 
     * @param userId the user ID
     */
    public void deleteUser(Long userId) {
        logger.info("Deleting user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        user.setIsDeleted(true);
        user.setIsActive(false);
        userRepository.save(user);
        logger.info("User deleted successfully: {}", userId);
    }

    /**
     * Get user by ID
     * 
     * @param userId the user ID
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long userId) {
        logger.debug("Getting user by ID: {}", userId);
        return userRepository.findById(userId);
    }

    /**
     * Get user by username
     * 
     * @param username the username
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        logger.debug("Getting user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Get user by email
     * 
     * @param email the email
     * @return Optional containing the user if found
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        logger.debug("Getting user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Get all users with pagination
     * 
     * @param pageable pagination information
     * @return Page of users
     */
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        logger.debug("Getting all active users with pagination");
        return userRepository.findByIsDeletedFalse(pageable);
    }

    /**
     * Search users by name or email
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return Page of users matching the search term
     */
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String searchTerm, Pageable pageable) {
        logger.debug("Searching users with term: {}", searchTerm);
        return userRepository.searchUsers(searchTerm, pageable);
    }

    /**
     * Update user's last login time
     * 
     * @param userId the user ID
     */
    public void updateLastLoginTime(Long userId) {
        logger.debug("Updating last login time for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        user.setLastLoginAt(new Date());
        userRepository.save(user);
    }

    /**
     * Validate user credentials
     * 
     * @param username the username
     * @param password the password
     * @return Optional containing the user if credentials are valid
     */
    @Transactional(readOnly = true)
    public Optional<User> validateCredentials(String username, String password) {
        logger.debug("Validating credentials for user: {}", username);
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Check if user is active
            if (!user.isActive()) {
                logger.warn("Login attempt for inactive user: {}", username);
                return Optional.empty();
            }
            
            // Validate password
            if (passwordEncoder.matches(password, user.getPassword())) {
                logger.debug("Credentials validated successfully for user: {}", username);
                return userOpt;
            }
        }
        
        logger.warn("Invalid credentials for user: {}", username);
        return Optional.empty();
    }

    /**
     * Validate user data
     * 
     * @param user the user to validate
     */
    private void validateUserData(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new RuntimeException("First name is required");
        }
        
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new RuntimeException("Last name is required");
        }
    }

    /**
     * Process forgot password request
     * 
     * @param email the email address
     * @return ForgotPasswordResponse with status
     */
    public ForgotPasswordResponse processForgotPassword(String email) {
        logger.info("Processing forgot password request for email: {}", email);
        
        try {
            // Find user by email
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                logger.warn("Forgot password request for non-existent email: {}", email);
                return ForgotPasswordResponse.builder()
                        .success(false)
                        .message("If the email exists in our system, you will receive a password reset email.")
                        .email(email)
                        .build();
            }
            
            User user = userOpt.get();
            
            // Check if user is active
            if (!user.isActive()) {
                logger.warn("Forgot password request for inactive user: {}", email);
                return ForgotPasswordResponse.builder()
                        .success(false)
                        .message("Account is inactive. Please contact administrator.")
                        .email(email)
                        .build();
            }
            
            // Generate new custom password
            String newPassword = PasswordGenerator.generateCustomPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            
            // Update user password in database
            user.setPassword(encodedPassword);
            user.setUpdatedAt(new Date());
            userRepository.save(user);
            
            // Send email with new password
            String emailSubject = "Password Reset - CRM System";
            String emailBody = buildPasswordResetEmailBody(user.getFirstName(), user.getUsername(), newPassword);
            
            boolean emailSent = emailNotificationConfig.sendEmailNotification(emailBody, emailSubject, email);
            
            if (emailSent) {
                logger.info("Password reset email sent successfully to: {}", email);
                return ForgotPasswordResponse.builder()
                        .success(true)
                        .message("Password reset email sent successfully. Please check your email.")
                        .email(email)
                        .build();
            } else {
                logger.error("Failed to send password reset email to: {}", email);
                return ForgotPasswordResponse.builder()
                        .success(false)
                        .message("Failed to send password reset email. Please try again later.")
                        .email(email)
                        .build();
            }
            
        } catch (Exception e) {
            logger.error("Error processing forgot password request for email: {}", email, e);
            return ForgotPasswordResponse.builder()
                    .success(false)
                    .message("An error occurred while processing your request. Please try again later.")
                    .email(email)
                    .build();
        }
    }
    
    /**
     * Build password reset email body
     * 
     * @param firstName the user's first name
     * @param username the username
     * @param newPassword the new password
     * @return the email body HTML content
     */
    private String buildPasswordResetEmailBody(String firstName, String username, String newPassword) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Password Reset - CRM System</title>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f8f9fa; }
                        .password-box { background-color: #e9ecef; padding: 15px; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; padding: 20px; color: #6c757d; font-size: 14px; }
                        .warning { background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 20px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Password Reset</h1>
                            <p>CRM System</p>
                        </div>
                        <div class="content">
                            <p>Dear %s,</p>
                            <p>We received a request to reset your password for your CRM System account.</p>
                            <p>Your new login credentials are:</p>
                            <div class="password-box">
                                <strong>Username:</strong> %s<br>
                                <strong>New Password:</strong> %s
                            </div>
                            <div class="warning">
                                <strong>Important:</strong> Please change your password after logging in for security purposes.
                            </div>
                            <p>If you did not request this password reset, please contact your system administrator immediately.</p>
                            <p>Best regards,<br>CRM System Team</p>
                        </div>
                        <div class="footer">
                            <p>This is an automated message. Please do not reply to this email.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(firstName, username, newPassword);
    }

    /**
     * Get all active users for dropdown
     * 
     * @return list of active users
     */
    @Transactional(readOnly = true)
    public List<User> getAllActiveUsers() {
        logger.debug("Getting all active users for dropdown");
        return userRepository.findByIsActiveTrueAndIsDeletedFalse();
    }

    /**
     * Get all users (active and inactive) for admin view
     * 
     * @param pageable pagination information
     * @return Page of all users
     */
    @Transactional(readOnly = true)
    public Page<User> getAllUsersForAdmin(Pageable pageable) {
        logger.debug("Getting all users (including inactive) for admin");
        return userRepository.findAll(pageable);
    }

    /**
     * Get users filtered by active status
     * 
     * @param isActive active status filter
     * @param pageable pagination information
     * @return Page of filtered users
     */
    @Transactional(readOnly = true)
    public Page<User> getUsersByActiveStatus(Boolean isActive, Pageable pageable) {
        logger.debug("Getting users by active status: {}", isActive);
        if (isActive == null) {
            return userRepository.findByIsDeletedFalse(pageable);
        }
        return userRepository.findByIsDeletedFalseAndIsActive(isActive, pageable);
    }
} 