package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;




@Getter
@Setter
public class JobApplicationDTO {

    private Long id;
    private Long memberId;
    private Long jobId;
    private String companyName;
    private String jobTitle;
    private String applicantName;
    private String phone;
    private String email;
    private String employmentType;
    private String status;
    private String createdAt;
    private String updatedAt;
    private String coverLetter;

    private Long companyMemberId;
    private String existsYn;

}
