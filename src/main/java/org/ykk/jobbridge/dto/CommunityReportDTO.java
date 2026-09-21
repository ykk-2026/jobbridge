package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;




@Getter
@Setter
public class CommunityReportDTO {

    private Long id;
    private Long postId;
    private Long reporterMemberId;
    private String reason;
    private String detail;
    private String status;
    private String createdAt;

    private String existsYn;

}
