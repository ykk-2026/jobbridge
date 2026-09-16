package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Param;
import org.ykk.jobbridge.dto.JoinRequest;

public interface MemberMapper {

    int countByLoginId(@Param("loginId") String loginId);

    int countByEmail(@Param("email") String email);

    int insertMember(JoinRequest request);

    int insertJobSeekerProfile(JoinRequest request);
}
