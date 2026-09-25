package org.ykk.jobbridge.util;

import java.util.Locale;

public final class SalaryTypeCodes {

    public static final String HOURLY = "HOURLY";
    public static final String DAILY = "DAILY";
    public static final String WEEKLY = "WEEKLY";
    public static final String MONTHLY = "MONTHLY";
    public static final String ANNUAL = "ANNUAL";
    public static final String NEGOTIABLE = "NEGOTIABLE";

    private SalaryTypeCodes() {
    }

    public static String normalize(String value) {
        String raw = CmmUtil.nvl(value).trim();
        if (raw.isEmpty()) return NEGOTIABLE;

        String upper = raw.toUpperCase(Locale.ROOT);
        if (upper.contains("시급") || upper.equals(HOURLY)) return HOURLY;
        if (upper.contains("일급") || upper.equals(DAILY)) return DAILY;
        if (upper.contains("주급") || upper.equals(WEEKLY)) return WEEKLY;
        if (upper.contains("월급") || upper.equals(MONTHLY)) return MONTHLY;
        if (upper.contains("연봉") || upper.equals(ANNUAL)) return ANNUAL;
        return NEGOTIABLE;
    }

    public static Integer toAnnualTenThousandWon(Long amount, String salaryType) {
        if (amount == null || amount <= 0) return null;

        double annualWon = switch (normalize(salaryType)) {
            case HOURLY -> amount * 209d * 12d;
            case DAILY -> amount * 261d;
            case WEEKLY -> amount * 52d;
            case MONTHLY -> amount * 12d;
            case ANNUAL -> amount;
            default -> 0d;
        };
        if (annualWon <= 0) return null;
        return (int) Math.round(annualWon / 10_000d);
    }
}
