package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.CompanyProfileDTO;
import org.ykk.jobbridge.dto.MemberDTO;

public interface ICompanyService {

    int insertCompanyInfo(MemberDTO pDTO, CompanyProfileDTO cDTO) throws Exception;

    CompanyProfileDTO getCompanyInfo(CompanyProfileDTO pDTO) throws Exception;

}
