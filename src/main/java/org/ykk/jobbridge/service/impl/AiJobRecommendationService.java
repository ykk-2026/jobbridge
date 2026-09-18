package org.ykk.jobbridge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.mapper.IAiJobRecommendationMapper;
import org.ykk.jobbridge.recommendation.JobRecommendationCalculator;
import org.ykk.jobbridge.service.IAiJobRecommendationService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiJobRecommendationService implements IAiJobRecommendationService {

    private final IAiJobRecommendationMapper recommendationMapper;

    // 프로필과 공고를 비교해 100점 만점의 점수를 계산하는 객체
    private final JobRecommendationCalculator calculator;

    @Transactional
    @Override
    public List<AiJobRecommendationDTO> getRecommendationList(AiJobRecommendationDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getRecommendationList Start!");

        List<AiJobRecommendationDTO> rList = new ArrayList<>();

        // 1. 구직자 프로필 조회 (프로필이 없으면 추천 계산 불가)
        JobSeekerProfileDTO profileDTO = recommendationMapper.getProfileInfo(pDTO);

        if (profileDTO == null) {
            log.info("구직자 프로필 없음 : " + pDTO.getMemberId());

        } else {
            // 2. 추천 대상 채용공고 조회 (OPEN 상태, 마감 전)
            List<JobPostingDTO> jobList = recommendationMapper.getJobList();

            // 3. 공고마다 추천 점수 계산
            for (JobPostingDTO jobDTO : jobList) {
                rList.add(calculator.calculate(pDTO.getMemberId(), profileDTO, jobDTO));
            }

            // 4. 총점 높은 순, 같으면 공고 번호 순으로 정렬
            rList.sort(Comparator.comparing(AiJobRecommendationDTO::getTotalScore).reversed()
                    .thenComparing(AiJobRecommendationDTO::getJobId));

            // 5. 계산 결과를 DB에 저장 (이미 저장된 회원+공고 조합이면 수정, 없으면 등록)
            for (AiJobRecommendationDTO rDTO : rList) {
                if (recommendationMapper.updateRecommendation(rDTO) == 0) {
                    recommendationMapper.insertRecommendation(rDTO);
                }
            }
        }

        log.info(this.getClass().getName() + ".getRecommendationList End!");

        return rList;
    }
}
