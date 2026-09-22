package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

import java.util.List;




@Mapper
public interface IAiJobRecommendationMapper {


    JobSeekerProfileDTO getProfileInfo(AiJobRecommendationDTO pDTO) throws Exception;


    List<JobPostingDTO> getJobList() throws Exception;


    int updateRecommendation(AiJobRecommendationDTO pDTO) throws Exception;


    int insertRecommendation(AiJobRecommendationDTO pDTO) throws Exception;

}
