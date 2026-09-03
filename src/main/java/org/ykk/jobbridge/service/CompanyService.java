package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.CompanyLoginDTO;
import org.ykk.jobbridge.dto.CompanyProfileDTO;
import org.ykk.jobbridge.dto.CompanySignupDTO;
import org.ykk.jobbridge.dto.SessionMember;

public interface CompanyService {

    void signup(CompanySignupDTO signup);

    CompanyLoginResult login(CompanyLoginDTO login);

    record CompanyLoginResult(SessionMember member, CompanyProfileDTO profile) {
    }
}
