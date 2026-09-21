package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * JOB_SEEKER_PROFILE 테이블(구직자 프로필)과 매핑되는 DTO
 * MEMBER 테이블의 기본 정보도 함께 담아 화면에 전달함
 */
@Getter
@Setter
public class JobSeekerProfileDTO {

    // MEMBER 테이블 정보
    private Long memberId; // 회원 고유번호
    private String name; // 이름
    private String birthDate; // 생년월일
    private String gender; // 성별
    private String email; // 이메일
    private String phone; // 전화번호

    // JOB_SEEKER_PROFILE 테이블 정보
    private Long profileId; // 프로필 기본키
    private String profileImageUrl; // 프로필 이미지
    private String residenceRegion; // 거주 지역
    private String desiredJob; // 희망 직무
    private String desiredRegion; // 희망 지역
    private String employmentType; // 희망 고용형태
    private String careerType; // 경력 구분(신입, 경력)
    private Integer careerYears; // 경력 연수
    private Integer minSalary; // 희망 최소 연봉
    private String workType; // 희망 근무방식(OFFICE, REMOTE, HYBRID, ANY)

    private Boolean wheelchairRequired; // 휠체어 접근 필요
    private Boolean accessibleRestroomRequired; // 장애인 화장실 필요
    private Boolean disabledParkingRequired; // 장애인 주차 필요
    private Boolean assistiveDeviceRequired; // 보조공학기기 필요
    private Boolean restAreaRequired; // 장애인 휴게공간 필요
    private Boolean elevatorRequired; // 엘리베이터 이용 필요

    private String contactTimeStart; // 연락 가능 시작 시간
    private String contactTimeEnd; // 연락 가능 종료 시간
    private String contactMethod; // 연락 방법

    private String introduction; // 자기소개
    private Boolean profilePublic; // 프로필 공개 여부

    private String createdAt; // 등록일
    private String updatedAt; // 수정일

    private String existsYn; // 프로필 존재 여부(Y/N)

}
