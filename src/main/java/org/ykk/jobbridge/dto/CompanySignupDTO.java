package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CompanySignupDTO {
    private Long id;
    private String loginId;
    private String password;
    private String passwordConfirm;
    private String name;
    private String email;
    private String phone;
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
    private LocalDate establishedDate;
}
