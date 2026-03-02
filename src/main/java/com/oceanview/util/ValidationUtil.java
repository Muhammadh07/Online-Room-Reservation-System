package com.oceanview.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ValidationUtil {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) return true; // email optional in some places
        return email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone)) return false;
        return phone.matches("^[0-9+\\- ]{7,15}$");
    }

    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isCheckOutAfterCheckIn(String checkIn, String checkOut) {
        try {
            LocalDate ci = LocalDate.parse(checkIn);
            LocalDate co = LocalDate.parse(checkOut);
            return co.isAfter(ci);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isCheckInNotPast(String checkIn) {
        try {
            LocalDate ci = LocalDate.parse(checkIn);
            return !ci.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPositiveInt(String val) {
        try {
            return Integer.parseInt(val) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNonNegativeDouble(String val) {
        try {
            return Double.parseDouble(val) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPositiveDouble(String val) {
        try {
            return Double.parseDouble(val) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("[<>\"']", "");
    }
}