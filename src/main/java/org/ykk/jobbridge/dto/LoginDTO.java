package org.ykk.jobbridge.dto;

public class LoginDTO {

    // 로그인 화면에서 입력한 값을 저장한다.
    private String loginId;
    private String password;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
