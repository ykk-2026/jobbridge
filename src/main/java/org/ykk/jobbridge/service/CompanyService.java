package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.CompanyLoginDTO;
import org.ykk.jobbridge.dto.CompanyLoginResultDTO;
import org.ykk.jobbridge.dto.CompanyProfileDTO;
import org.ykk.jobbridge.dto.CompanySignupDTO;

public interface CompanyService {

    void signup(CompanySignupDTO signup);

    CompanyLoginResultDTO login(CompanyLoginDTO login);
}
