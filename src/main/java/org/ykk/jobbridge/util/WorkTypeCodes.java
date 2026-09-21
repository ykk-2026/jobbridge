package org.ykk.jobbridge.util;

import java.util.Locale;
import java.util.Set;

public final class WorkTypeCodes {

    private static final Set<String> VALUES = Set.of("OFFICE", "REMOTE", "HYBRID", "ANY");

    private WorkTypeCodes() {
    }

    public static String normalize(String value) {
        String code = CmmUtil.nvl(value, "ANY").trim().toUpperCase(Locale.ROOT);
        return VALUES.contains(code) ? code : "ANY";
    }
}
