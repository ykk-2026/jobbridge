package org.ykk.jobbridge.recommendation;

import org.ykk.jobbridge.dto.JobPostingDTO;
import org.ykk.jobbridge.dto.JobSeekerProfileDTO;

import java.util.Optional;

/**
 * 생성형 AI로 직무 적합도를 평가하는 서비스 인터페이스
 * 구현체 : OpenAiJobMatchService
 */
public interface IAiJobMatchService {

    /** 직무·기술 적합도 평가 결과. source는 GENERATIVE_AI 또는 RULE_FALLBACK. */
    record JobMatchAssessment(int score, String reason, String source) {
    }

    /**
     * 구직자 프로필과 채용공고의 직무·기술 적합도(0~30점) 평가
     *
     * @return 평가 결과, API 키가 없거나 호출 실패 시 Optional.empty()
     */
    Optional<JobMatchAssessment> assess(JobSeekerProfileDTO profile, JobPostingDTO job);
}
