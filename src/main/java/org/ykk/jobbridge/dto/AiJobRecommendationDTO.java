package org.ykk.jobbridge.dto;

public class AiJobRecommendationDTO {
    private Long memberId;
    private Long jobId;
    private Integer totalScore;
    private Integer jobScore;
    private Integer regionScore;
    private Integer employmentTypeScore;
    private Integer careerScore;
    private Integer salaryScore;
    private Integer workStyleScore;
    private Integer accessibilityScore;
    private String recommendationReason;
    private String mismatchReason;
    private JobPostingDTO job;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
    public Integer getJobScore() { return jobScore; }
    public void setJobScore(Integer jobScore) { this.jobScore = jobScore; }
    public Integer getRegionScore() { return regionScore; }
    public void setRegionScore(Integer regionScore) { this.regionScore = regionScore; }
    public Integer getEmploymentTypeScore() { return employmentTypeScore; }
    public void setEmploymentTypeScore(Integer employmentTypeScore) { this.employmentTypeScore = employmentTypeScore; }
    public Integer getCareerScore() { return careerScore; }
    public void setCareerScore(Integer careerScore) { this.careerScore = careerScore; }
    public Integer getSalaryScore() { return salaryScore; }
    public void setSalaryScore(Integer salaryScore) { this.salaryScore = salaryScore; }
    public Integer getWorkStyleScore() { return workStyleScore; }
    public void setWorkStyleScore(Integer workStyleScore) { this.workStyleScore = workStyleScore; }
    public Integer getAccessibilityScore() { return accessibilityScore; }
    public void setAccessibilityScore(Integer accessibilityScore) { this.accessibilityScore = accessibilityScore; }
    public String getRecommendationReason() { return recommendationReason; }
    public void setRecommendationReason(String recommendationReason) { this.recommendationReason = recommendationReason; }
    public String getMismatchReason() { return mismatchReason; }
    public void setMismatchReason(String mismatchReason) { this.mismatchReason = mismatchReason; }
    public JobPostingDTO getJob() { return job; }
    public void setJob(JobPostingDTO job) { this.job = job; }
}
