package org.ykk.jobbridge.util;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class EducationLevelCodes {

    public static final Set<String> VALUES = Set.of(
            "ANY", "ELEMENTARY_SCHOOL", "MIDDLE_SCHOOL", "HIGH_SCHOOL",
            "COLLEGE", "UNIVERSITY", "MASTER", "DOCTOR");

    private static final Map<String, String> ALIASES = Map.ofEntries(
            Map.entry("무관", "ANY"),
            Map.entry("학력 무관", "ANY"),
            Map.entry("초졸", "ELEMENTARY_SCHOOL"),
            Map.entry("초졸 이상", "ELEMENTARY_SCHOOL"),
            Map.entry("중졸", "MIDDLE_SCHOOL"),
            Map.entry("중졸 이상", "MIDDLE_SCHOOL"),
            Map.entry("고졸", "HIGH_SCHOOL"),
            Map.entry("고졸 이상", "HIGH_SCHOOL"),
            Map.entry("초대졸", "COLLEGE"),
            Map.entry("초대졸 이상", "COLLEGE"),
            Map.entry("전문대졸", "COLLEGE"),
            Map.entry("전문대졸 이상", "COLLEGE"),
            Map.entry("대졸", "UNIVERSITY"),
            Map.entry("대졸 이상", "UNIVERSITY"),
            Map.entry("석사", "MASTER"),
            Map.entry("석사 이상", "MASTER"),
            Map.entry("박사", "DOCTOR"),
            Map.entry("박사 이상", "DOCTOR"));

    private EducationLevelCodes() {
    }

    public static String normalize(String value) {
        String raw = CmmUtil.nvl(value).trim();
        if (raw.isEmpty()) return "ANY";

        String upper = raw.toUpperCase(Locale.ROOT);
        String code = ALIASES.getOrDefault(raw, upper);
        if (!VALUES.contains(code)) {
            throw new IllegalArgumentException("지원하지 않는 학력 조건입니다: " + value);
        }
        return code;
    }

    public static int rank(String value) {
        return switch (normalize(value)) {
            case "ELEMENTARY_SCHOOL" -> 1;
            case "MIDDLE_SCHOOL" -> 2;
            case "HIGH_SCHOOL" -> 3;
            case "COLLEGE" -> 4;
            case "UNIVERSITY" -> 5;
            case "MASTER" -> 6;
            case "DOCTOR" -> 7;
            default -> 0;
        };
    }
}
