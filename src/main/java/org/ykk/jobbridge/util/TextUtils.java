package org.ykk.jobbridge.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtils {

    private static final Pattern FIRST_NUMBER = Pattern.compile("\\d+");

    private TextUtils() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    public static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    public static String compact(String value) {
        return value == null ? "" : value.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }

    public static String toCode(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    public static boolean isUnrestricted(String value) {
        if (isBlank(value)) return true;
        String trimmed = value.trim();
        return "ANY".equalsIgnoreCase(trimmed) || "무관".equals(trimmed);
    }

    public static Integer firstNumber(String value) {
        if (value == null) return null;
        Matcher matcher = FIRST_NUMBER.matcher(value);
        return matcher.find() ? Integer.valueOf(matcher.group()) : null;
    }
}
