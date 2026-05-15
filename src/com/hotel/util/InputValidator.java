package com.hotel.util;

import java.time.LocalDate;

public class InputValidator {

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("01\\d{9}");
    }

    public static boolean isPositiveNumber(String value) {
        try {
            return Double.parseDouble(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isDateAfter(String dateA, String dateB) {
        try {
            return LocalDate.parse(dateA).isAfter(LocalDate.parse(dateB));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isTodayOrFuture(String date) {
        try {
            return !LocalDate.parse(date).isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
}
