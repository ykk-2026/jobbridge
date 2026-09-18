package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.CompanyProfileDTO;
import org.ykk.jobbridge.dto.MemberDTO;

public interface ICompanyService {

    /**
     * 기업회원 가입 (MEMBER 테이블 + COMPANY_PROFILE 테이블 저장)
     *
     * @param pDTO 담당자 회원정보
     * @param cDTO 기업정보
     * @return 1 : 성공 / 2 : 이미 가입된 아이디, 이메일, 사업자등록번호 / 0 : 실패
     */
    int insertCompanyInfo(MemberDTO pDTO, CompanyProfileDTO cDTO) throws Exception;

    /**
     * 기업정보 조회
     *
     * @param pDTO 조회할 memberId 값
     * @return 기업정보(담당자 아이디, 이름 포함)
     */
    CompanyProfileDTO getCompanyInfo(CompanyProfileDTO pDTO) throws Exception;

}
