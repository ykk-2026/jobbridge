package org.ykk.jobbridge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

import java.util.List;

/**
 * AiJobRecommendationMapper.xml과 매핑되는 인터페이스
 */
@Mapper
public interface IAiJobRecommendationMapper {

    // 추천 계산에 사용할 구직자 프로필 조회
    JobSeekerProfileDTO getProfileInfo(AiJobRecommendationDTO pDTO) throws Exception;

    // 추천 대상 채용공고 리스트(OPEN 상태이고 마감되지 않은 공고)
    List<JobPostingDTO> getJobList() throws Exception;

    // 추천 결과 수정(이미 저장된 경우)
    int updateRecommendation(AiJobRecommendationDTO pDTO) throws Exception;

    // 추천 결과 등록
    int insertRecommendation(AiJobRecommendationDTO pDTO) throws Exception;

}
