package org.ykk.jobbridge.util;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class EmploymentTypeCodes {

    public static final Set<String> VALUES = Set.of(
            "ANY", "FULL_TIME", "REGULAR_EMPLOYEE", "CONTRACT", "PERMANENT_CONTRACT", "CONVERSION_TYPE",
            "PART_TIME", "INTERN", "DISPATCH", "FREELANCE");

    private static final Map<String, String> ALIASES = Map.ofEntries(
            Map.entry("INTERNSHIP", "INTERN"),
            Map.entry("FULL_TIME_CONVERSION", "CONVERSION_TYPE"),
            Map.entry("FREELANCER", "FREELANCE"),
            Map.entry("무관", "ANY"),
            Map.entry("정규직", "FULL_TIME"),
            Map.entry("상용직", "REGULAR_EMPLOYEE"),
            Map.entry("계약직", "CONTRACT"),
            Map.entry("무기계약직", "PERMANENT_CONTRACT"),
            Map.entry("정규직 전환형", "CONVERSION_TYPE"),
            Map.entry("시간제·파트타임", "PART_TIME"),
            Map.entry("시간제", "PART_TIME"),
            Map.entry("파트타임", "PART_TIME"),
            Map.entry("알바", "PART_TIME"),
            Map.entry("아르바이트", "PART_TIME"),
            Map.entry("인턴", "INTERN"),
            Map.entry("인턴십", "INTERN"),
            Map.entry("파견직", "DISPATCH"),
            Map.entry("프리랜서", "FREELANCE"));

    private EmploymentTypeCodes() {
    }

    public static String normalize(String value) {
        String raw = CmmUtil.nvl(value).trim();
        if (raw.isEmpty()) return "ANY";

        String upper = raw.toUpperCase(Locale.ROOT);
        String code = ALIASES.getOrDefault(raw, ALIASES.getOrDefault(upper, upper));
        if (!VALUES.contains(code)) {
            throw new IllegalArgumentException("지원하지 않는 고용형태입니다: " + value);
        }
        return code;
    }
}
