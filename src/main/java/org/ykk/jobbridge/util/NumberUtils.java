package org.ykk.jobbridge.util;

public final class NumberUtils {

    private NumberUtils() {
    }

    /** value를 [min, max] 범위 안으로 맞춘다. */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    public static int zeroIfNull(Integer value) {
        return value == null ? 0 : value;
    }
}
