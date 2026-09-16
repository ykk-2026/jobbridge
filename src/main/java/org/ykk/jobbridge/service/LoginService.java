package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.LoginDTO;
import org.ykk.jobbridge.dto.SessionMember;

public interface LoginService {

    SessionMember login(LoginDTO loginDTO);
}
