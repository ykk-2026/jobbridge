package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class JobSeekerProfileDTO {

    // member 테이블 정보
    private Long memberId;
    private String name;
    private LocalDate birthDate;
    private String gender;
    private String email;
    private String phone;

    // job_seeker_profile 테이블 정보
    private Long profileId;
    private String profileImageUrl;
    private String residenceRegion;
    private String desiredJob;
    private String desiredRegion;
    private String employmentType;
    private String careerType;
    private Integer careerYears;
    private Integer minSalary;

    private Boolean remotePreferred;
    private Boolean flexiblePreferred;
    private Boolean wheelchairRequired;
    private Boolean accessibleRestroomRequired;
    private Boolean disabledParkingRequired;
    private Boolean assistiveDeviceRequired;
    private Boolean hybridPreferred;
    private Boolean onsitePreferred;

    private LocalTime contactTimeStart;
    private LocalTime contactTimeEnd;
    private String contactMethod;

    private String introduction;
    private Boolean profilePublic;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
