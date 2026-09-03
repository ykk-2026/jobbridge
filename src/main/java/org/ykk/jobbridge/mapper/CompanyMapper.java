package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Param;
import org.ykk.jobbridge.dto.CompanyProfileDTO;
import org.ykk.jobbridge.dto.CompanySignupDTO;
import org.ykk.jobbridge.dto.MemberLoginResult;

public interface CompanyMapper {

    int countByLoginId(@Param("loginId") String loginId);

    int countByEmail(@Param("email") String email);

    int countByBusinessNumber(@Param("businessNumber") String businessNumber);

    int insertMember(CompanySignupDTO signup);

    int insertCompanyProfile(CompanySignupDTO signup);

    MemberLoginResult findMemberByLoginId(@Param("loginId") String loginId);

    CompanyProfileDTO findProfileByMemberId(@Param("memberId") Long memberId);
}
