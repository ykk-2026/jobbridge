package org.ykk.jobbridge.util;

import java.util.Objects;

/**
 * 자주 사용하는 함수 및 기능을 정의한 공통 유틸
 * 언제든지 호출할 수 있게 static으로 메모리에 올림
 */
public class CmmUtil {

    /**
     * DB의 nvl 함수와 동일한 기능
     * 값이 null 또는 빈값이면 chg_str로 변경함
     */
    public static String nvl(String str, String chg_str) {
        return (str == null || str.isEmpty()) ? chg_str : str;
    }

    public static String nvl(String str) {
        return nvl(str, "");
    }

    public static String checked(String str, String com_str) {
        return Objects.equals(str, com_str) ? " checked" : "";
    }

    public static String checked(String[] str, String com_str) {
        if (str == null) return ""; // null 방어

        for (String s : str) {
            if (Objects.equals(s, com_str)) {
                return " checked";
            }
        }
        return "";
    }

    public static String select(String str, String com_str) {
        return Objects.equals(str, com_str) ? " selected" : "";
    }
}
