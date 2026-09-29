package org.ykk.jobbridge.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ykk.jobbridge.dto.AiJobRecommendationDTO;
import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;
import org.ykk.jobbridge.mapper.IAiJobRecommendationMapper;
import org.ykk.jobbridge.recommendation.IAiJobMatchService;
import org.ykk.jobbridge.recommendation.IAiJobMatchService.JobMatchAssessment;
import org.ykk.jobbridge.recommendation.JobRecommendationCalculator;
import org.ykk.jobbridge.service.IAiJobRecommendationService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiJobRecommendationService implements IAiJobRecommendationService {

    private static final int AI_BATCH_SIZE = 100;

    private final IAiJobRecommendationMapper recommendationMapper;

    private final JobRecommendationCalculator calculator;

    private final IAiJobMatchService aiJobMatchService;

    @Transactional
    @Override
    public List<AiJobRecommendationDTO> getRecommendationList(AiJobRecommendationDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getRecommendationList Start!");

        List<AiJobRecommendationDTO> rList = new ArrayList<>();

        JobSeekerProfileDTO profileDTO = recommendationMapper.getProfileInfo(pDTO);

        if (profileDTO == null) {
            log.info("구직자 프로필 없음 : " + pDTO.getMemberId());

        } else {

            List<JobPostingDTO> jobList = recommendationMapper.getJobList();

            Map<Long, JobMatchAssessment> aiAssessments = new LinkedHashMap<>();
            for (int start = 0; start < jobList.size(); start += AI_BATCH_SIZE) {
                int end = Math.min(start + AI_BATCH_SIZE, jobList.size());
                aiAssessments.putAll(aiJobMatchService.assessBatch(profileDTO, jobList.subList(start, end)));
            }

            for (JobPostingDTO jobDTO : jobList) {
                rList.add(calculator.calculate(
                        pDTO.getMemberId(), profileDTO, jobDTO, aiAssessments.get(jobDTO.getId())));
            }

            rList.sort(Comparator.comparing(AiJobRecommendationDTO::getTotalScore).reversed()
                    .thenComparing(AiJobRecommendationDTO::getJobId));

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
