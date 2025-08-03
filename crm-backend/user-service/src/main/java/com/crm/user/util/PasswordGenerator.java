package com.crm.user.util;

import java.security.SecureRandom;

/**
 * Password Generator Utility
 * 
 * Utility class for generating secure custom passwords.
 * Used for forgot password functionality.
 * 
 * @version 2.0
 * @date December 2024
 * @author Gokul Annadurai
 */
public class PasswordGenerator {
    
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    
    private static final SecureRandom RANDOM = new SecureRandom();
    
    /**
     * Generate a custom password with specified length
     * 
     * @param length the length of the password
     * @return the generated password
     */
    public static String generateCustomPassword(int length) {
        if (length < 8) {
            length = 8; // Minimum length for security
        }
        
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each category
        password.append(UPPER_CASE.charAt(RANDOM.nextInt(UPPER_CASE.length())));
        password.append(LOWER_CASE.charAt(RANDOM.nextInt(LOWER_CASE.length())));
        password.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        password.append(SPECIAL_CHARS.charAt(RANDOM.nextInt(SPECIAL_CHARS.length())));
        
        // Fill the rest with random characters
        String allChars = UPPER_CASE + LOWER_CASE + NUMBERS + SPECIAL_CHARS;
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(RANDOM.nextInt(allChars.length())));
        }
        
        // Shuffle the password to make it more random
        return shuffleString(password.toString());
    }
    
    /**
     * Generate a default custom password (12 characters)
     * 
     * @return the generated password
     */
    public static String generateCustomPassword() {
        return generateCustomPassword(12);
    }
    
    /**
     * Shuffle a string to make it more random
     * 
     * @param input the input string
     * @return the shuffled string
     */
    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
} 