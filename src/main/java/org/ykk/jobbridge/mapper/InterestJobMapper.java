package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Param;
import org.ykk.jobbridge.dto.InterestJobDTO;

import java.util.List;

public interface InterestJobMapper {
    List<InterestJobDTO> findAll(@Param("memberId") Long memberId);

    int countByCompanyAndTitle(@Param("memberId") Long memberId,
                               @Param("companyName") String companyName,
                               @Param("title") String title);

    int insert(InterestJobDTO interestJob);

    int deleteByCompanyAndTitle(@Param("memberId") Long memberId,
                                @Param("companyName") String companyName,
                                @Param("title") String title);
}
