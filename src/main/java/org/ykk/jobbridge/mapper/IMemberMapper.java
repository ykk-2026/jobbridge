package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.MemberDTO;

@Mapper
public interface IMemberMapper {

    MemberDTO getLoginIdExists(MemberDTO pDTO) throws Exception;

    MemberDTO getEmailExists(MemberDTO pDTO) throws Exception;

    int insertMemberInfo(MemberDTO pDTO) throws Exception;

    int insertJobSeekerProfile(MemberDTO pDTO) throws Exception;

    MemberDTO getLogin(MemberDTO pDTO) throws Exception;

}
