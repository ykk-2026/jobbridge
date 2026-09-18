package org.ykk.jobbridge.service;

import org.ykk.jobbridge.dto.AiJobRecommendationDTO;

import java.util.List;

public interface IAiJobRecommendationService {

    /**
     * 구직자 프로필과 채용공고를 비교하여 추천 점수를 계산하고, 결과를 DB에 저장한 뒤 돌려줌
     *
     * @param pDTO 구직자 회원 고유번호(memberId)
     * @return 총점 높은 순으로 정렬된 추천 리스트 (프로필이 없으면 빈 리스트)
     */
    List<AiJobRecommendationDTO> getRecommendationList(AiJobRecommendationDTO pDTO) throws Exception;

}
