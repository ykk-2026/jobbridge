package org.ykk.jobbridge.util;

public final class NumberUtils {

    private NumberUtils() {
    }


    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static int zeroIfNull(Integer value) {
        return value == null ? 0 : value;
    }
}
