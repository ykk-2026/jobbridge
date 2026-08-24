package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobApplicationDTO {
    private Long id;
    private Long memberId;
    private String jobId;
    private String companyName;
    private String jobTitle;
    private String applicantName;
    private String phone;
    private String email;
    private String employmentType;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
