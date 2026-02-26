package com.hotel.util;

import java.time.LocalDate;

/**
 * ValidationUtil — static helpers for input validation.
 */
public final class ValidationUtil {

    private ValidationUtil() {}

    public static boolean isNotEmpty(String v)   { return v != null && !v.trim().isEmpty(); }

    public static boolean isValidEmail(String e) {
        return isNotEmpty(e) && e.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidPhone(String p) {
        return isNotEmpty(p) && p.replaceAll("[\\s\\-+()]", "").matches("\\d{7,15}");
    }

    public static boolean isValidPassword(String p) {
        return isNotEmpty(p) && p.length() >= 6;
    }

    public static boolean isValidDate(String d) {
        try { LocalDate.parse(d); return true; } catch (Exception e) { return false; }
    }

    public static boolean isFutureOrToday(String d) {
        try { return !LocalDate.parse(d).isBefore(LocalDate.now()); } catch (Exception e) { return false; }
    }

    public static boolean isCheckoutAfterCheckin(String cin, String cout) {
        try { return LocalDate.parse(cout).isAfter(LocalDate.parse(cin)); } catch (Exception e) { return false; }
    }

    public static String sanitize(String v) {
        return v == null ? "" : v.trim().replaceAll("[<>\"']", "");
    }
}