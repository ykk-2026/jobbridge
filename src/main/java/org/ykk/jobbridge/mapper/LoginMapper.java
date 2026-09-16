package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Param;
import org.ykk.jobbridge.dto.MemberLoginResult;

public interface LoginMapper {

    MemberLoginResult findByLoginId(@Param("loginId") String loginId);
}
