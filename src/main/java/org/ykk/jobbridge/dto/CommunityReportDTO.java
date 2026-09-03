package org.ykk.jobbridge.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommunityReportDTO {
    private Long id;
    private Long postId;
    private Long reporterMemberId;
    private String reason;
    private String detail;
    private String status;
    private LocalDateTime createdAt;
}
