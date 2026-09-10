package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Param;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.InterestJobDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

import java.util.List;

public interface AiJobRecommendationMapper {
    void ensureTable();

    JobSeekerProfileDTO findProfile(@Param("memberId") Long memberId);

    List<InterestJobDTO> findEligibleJobs();

    int updateRecommendation(AiJobRecommendationDTO recommendation);

    int insertRecommendation(AiJobRecommendationDTO recommendation);
}
