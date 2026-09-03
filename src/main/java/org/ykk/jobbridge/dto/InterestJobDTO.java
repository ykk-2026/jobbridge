package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class InterestJobDTO {
    private Long id;
    private Long memberId;
    private String companyName;
    private String title;
    private String jobCategory;
    private String employmentType;
    private String location;
    private Integer salaryMin;
    private Integer salaryMax;
    private String experienceLevel;
    private String educationLevel;
    private String description;
    private String requirements;
    private String preferredQualifications;
    private String accessibilityInfo;
    private Boolean wheelchairAccessible;
    private Boolean accessibleRestroom;
    private Boolean disabledParking;
    private Boolean remoteAvailable;
    private Boolean flexibleWorkAvailable;
    private Boolean assistiveDeviceSupport;
    private LocalDate deadline;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
