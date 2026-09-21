package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * AI_JOB_POSTING_RECOMMENDATION 테이블(AI 채용공고 추천 결과)과 매핑되는 DTO
 */
@Getter
@Setter
public class AiJobRecommendationDTO {

    private Long memberId; // 구직자 회원 고유번호
    private Long jobId; // 채용공고 번호
    private Integer totalScore; // 총점(100점 만점)
    private Integer jobScore; // 직무·기술 적합도(30점)
    private String jobMatchSource; // 직무 점수 출처(GENERATIVE_AI, RULE_FALLBACK)
    private String jobMatchReason; // 직무 점수 근거
    private Integer regionScore; // 지역·출퇴근(15점)
    private Integer employmentTypeScore; // 고용형태(10점)
    private Integer careerScore; // 경력(10점)
    private Integer salaryScore; // 급여(10점)
    private Integer workStyleScore; // 근무방식(10점)
    private Integer accessibilityScore; // 접근성(10점)
    private String recommendationReason; // 추천 이유
    private String mismatchReason; // 부족한 조건

    private JobPostingDTO job; // 추천된 채용공고 정보

}
