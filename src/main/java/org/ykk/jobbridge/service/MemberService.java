package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.JoinRequest;

public interface MemberService {

    void join(JoinRequest request);

    boolean isLoginIdAvailable(String loginId);
}
