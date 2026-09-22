package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.MemberDTO;

public interface IMemberService {

    MemberDTO getLoginIdExists(MemberDTO pDTO) throws Exception;

    MemberDTO getEmailExists(MemberDTO pDTO) throws Exception;

    int insertMemberInfo(MemberDTO pDTO) throws Exception;

    MemberDTO getLogin(MemberDTO pDTO) throws Exception;

}
