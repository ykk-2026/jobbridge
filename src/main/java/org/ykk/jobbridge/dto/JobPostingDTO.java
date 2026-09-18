package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * JOB_POSTING 테이블(채용공고)과 매핑되는 DTO
 */
@Getter
@Setter
public class JobPostingDTO {

    private Long id; // 기본키, 공고 번호
    private Long companyMemberId; // 공고를 등록한 기업회원 고유번호
    private String companyName; // 회사명
    private String title; // 공고 제목
    private String jobCategory; // 직무
    private String employmentType; // 고용형태
    private String location; // 근무지
    private Integer salaryMin; // 최소 급여
    private Integer salaryMax; // 최대 급여
    private String experienceLevel; // 경력 조건
    private String educationLevel; // 학력 조건
    private String description; // 업무 내용
    private String requirements; // 자격 요건
    private String preferredQualifications; // 우대 사항
    private String accessibilityInfo; // 접근성 정보
    private Boolean wheelchairAccessible; // 휠체어 접근 가능
    private Boolean accessibleRestroom; // 장애인 화장실
    private Boolean disabledParking; // 장애인 주차
    private Boolean remoteAvailable; // 재택근무 가능
    private Boolean flexibleWorkAvailable; // 유연근무 가능
    private Boolean assistiveDeviceSupport; // 보조공학기기 지원
    private String deadline; // 마감일
    private String status; // 상태(OPEN, CLOSED, DELETED)
    private String createdAt; // 등록일
    private String updatedAt; // 수정일

}
