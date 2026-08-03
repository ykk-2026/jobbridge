package org.ykk.jobbridge.dto;

import java.io.Serializable;

public class SessionMember implements Serializable {

    private final Long id;
    private final String loginId;
    private final String name;
    private final String role;

    public SessionMember(
            Long id,
            String loginId,
            String name,
            String role
    ) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}