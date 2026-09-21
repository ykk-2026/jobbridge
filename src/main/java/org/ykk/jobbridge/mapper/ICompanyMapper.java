package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.CompanyProfileDTO;




@Mapper
public interface ICompanyMapper {


    CompanyProfileDTO getBusinessNumberExists(CompanyProfileDTO pDTO) throws Exception;


    int insertCompanyProfile(CompanyProfileDTO pDTO) throws Exception;


    CompanyProfileDTO getCompanyInfo(CompanyProfileDTO pDTO) throws Exception;

}
