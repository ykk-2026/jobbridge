package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.JoinDTO;

public interface JoinService {

    void join(JoinDTO joinDTO);

    boolean isLoginIdAvailable(String loginId);
}
