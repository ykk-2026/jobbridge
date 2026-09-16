package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Param;
import org.ykk.jobbridge.dto.JoinDTO;

public interface JoinMapper {

    int countByLoginId(@Param("loginId") String loginId);

    int countByEmail(@Param("email") String email);

    int insertMember(JoinDTO joinDTO);

    int insertJobSeekerProfile(JoinDTO joinDTO);
}
