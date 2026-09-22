package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiJobRecommendationDTO {

    private Long memberId;
    private Long jobId;
    private Integer totalScore;
    private Integer jobScore;
    private String jobMatchSource;
    private String jobMatchReason;
    private Integer regionScore;
    private Integer employmentTypeScore;
    private Integer careerScore;
    private Integer salaryScore;
    private Integer workStyleScore;
    private Integer accessibilityScore;
    private String recommendationReason;
    private String mismatchReason;

    private JobPostingDTO job;

}
