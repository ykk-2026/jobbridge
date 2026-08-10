package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.JoinRequest;
import org.ykk.jobbridge.dto.LoginRequest;
import org.ykk.jobbridge.dto.SessionMember;

public interface MemberService {

    void join(JoinRequest request);

    SessionMember login(LoginRequest request);

    boolean isLoginIdAvailable(String loginId);
}
