package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyProfileDTO {

    private Long id;
    private Long memberId;
    private String companyName;
    private String businessNumber;
    private String representativeName;
    private String industry;
    private String companyAddress;
    private String companyDetailAddress;
    private String companyPhone;
    private String websiteUrl;
    private String logoUrl;
    private String companyDescription;
    private Integer employeeCount;
    private String establishedDate;
    private String verificationStatus;
    private String createdAt;
    private String updatedAt;

    private String loginId;
    private String name;
    private String role;

    private String existsYn;

}
