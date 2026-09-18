package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 관심 공고 저장 정보와 job_posting 조인 결과를 전달하는 DTO.
 * interest_job에는 id, memberId, jobId, createdAt만 저장된다.
 */
@Getter
@Setter
public class InterestJobDTO {

    private Long id;
    private Long memberId;
    private Long jobId;

    // 아래 값은 interest_job에 저장하지 않고 job_posting에서 조회한다.
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
    private String deadline;
    private String status;

    private String createdAt; // 관심 공고로 등록한 시각
    private String updatedAt; // 채용 공고의 최종 수정 시각
    private String existsYn;
}
