package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * JOB_APPLICATION 테이블(입사 지원)과 매핑되는 DTO
 */
@Getter
@Setter
public class JobApplicationDTO {

    private Long id; // 기본키
    private Long memberId; // 지원한 구직자 회원 고유번호
    private Long jobId; // job_posting.id 외래키
    private String companyName; // job_posting/company_profile JOIN 조회값(테이블에 저장하지 않음)
    private String jobTitle; // job_posting.title JOIN 조회값(테이블에 저장하지 않음)
    private String applicantName; // 지원자 이름
    private String phone; // 전화번호
    private String email; // 이메일
    private String employmentType; // 고용형태
    private String status; // 지원 상태(APPLIED 등)
    private String createdAt; // 지원일
    private String updatedAt; // 수정일
    private String coverLetter;

    private Long companyMemberId; // 기업회원이 지원자 목록 조회할 때 사용
    private String existsYn; // 이미 지원한 공고인지 여부(Y/N)

}
