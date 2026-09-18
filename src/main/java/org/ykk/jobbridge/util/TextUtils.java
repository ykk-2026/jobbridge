package org.ykk.jobbridge.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 여러 서비스에서 반복되던 문자열 처리 공통 함수. */
public final class TextUtils {

    private static final Pattern FIRST_NUMBER = Pattern.compile("\\d+");

    private TextUtils() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /** null이면 빈 문자열, 아니면 그대로 돌려준다. */
    public static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    /** null이면 빈 문자열, 아니면 앞뒤 공백을 제거한다. */
    public static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    /** 공백을 모두 제거하고 소문자로 바꾼다. 텍스트 포함 여부를 비교할 때 사용한다. */
    public static String compact(String value) {
        return value == null ? "" : value.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }

    /** 앞뒤 공백을 제거하고 대문자로 바꾼다. FULL_TIME 같은 코드 값을 비교할 때 사용한다. */
    public static String toCode(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    /** 값이 비어 있거나 ANY / 무관이면 "조건 없음"으로 본다. */
    public static boolean isUnrestricted(String value) {
        if (isBlank(value)) return true;
        String trimmed = value.trim();
        return "ANY".equalsIgnoreCase(trimmed) || "무관".equals(trimmed);
    }

    /** 문자열에서 처음 나오는 숫자를 꺼낸다. 예: "경력 3년 이상" → 3, 숫자가 없으면 null. */
    public static Integer firstNumber(String value) {
        if (value == null) return null;
        Matcher matcher = FIRST_NUMBER.matcher(value);
        return matcher.find() ? Integer.valueOf(matcher.group()) : null;
    }
}
