package org.ykk.jobbridge.dto;

import java.time.LocalDateTime;

public class CommunityReportDTO {
    private Long id;
    private Long postId;
    private Long reporterMemberId;
    private String reason;
    private String detail;
    private String status;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getReporterMemberId() { return reporterMemberId; }
    public void setReporterMemberId(Long reporterMemberId) { this.reporterMemberId = reporterMemberId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
