package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobPostingDTO {

    private Long id;
    private Long companyMemberId;
    private String companyName;
    private String title;
    private String jobCategory;
    private String employmentType;
    private String location;
    private Integer salaryMin;
    private Long salaryAmount;
    private String salaryType;
    private String workType;
    private String experienceLevel;
    private String educationLevel;
    private String description;
    private String requirements;
    private String preferredQualifications;
    private String accessibilityInfo;
    private Boolean wheelchairAccessible;
    private Boolean accessibleRestroom;
    private Boolean disabledParking;
    private Boolean restAreaAvailable;
    private Boolean elevatorAvailable;
    private Boolean assistiveDeviceSupport;
    private Boolean accessibilityVerified;
    private String source;
    private String externalJobId;
    private String contactNumber;
    private String entryType;
    private String managingAgency;
    private String recruitmentStartDate;
    private String externalApplyDate;
    private String externalRegisteredDate;
    private String deadline;
    private String status;
    private String createdAt;
    private String updatedAt;

}
