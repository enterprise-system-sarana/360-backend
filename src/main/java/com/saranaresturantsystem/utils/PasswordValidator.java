package com.saranaresturantsystem.utils;

import java.util.regex.Pattern;

public class PasswordValidator {
    // Min 8 chars, at least 1 uppercase, 1 lowercase, 1 digit, 1 special character
    private static final String PASSWORD_PATTERN = 
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!_]).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    public static boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        return pattern.matcher(password).matches();
    }

    public static String getRequirementsMessage() {
        return "Password must be at least 8 characters long and contain at least one uppercase letter, " +
               "one lowercase letter, one digit, and one special character (@#$%^&+=!_).";
    }
}
