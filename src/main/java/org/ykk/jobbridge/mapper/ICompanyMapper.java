package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.CompanyProfileDTO;

/**
 * CompanyMapper.xml과 매핑되는 인터페이스
 */
@Mapper
public interface ICompanyMapper {

    // 사업자등록번호 중복 체크
    CompanyProfileDTO getBusinessNumberExists(CompanyProfileDTO pDTO) throws Exception;

    // 기업정보 등록(COMPANY_PROFILE 테이블)
    int insertCompanyProfile(CompanyProfileDTO pDTO) throws Exception;

    // 기업정보 조회(회원 고유번호 기준, MEMBER 테이블 JOIN)
    CompanyProfileDTO getCompanyInfo(CompanyProfileDTO pDTO) throws Exception;

}
