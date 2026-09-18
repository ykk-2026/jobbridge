package org.ykk.jobbridge.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * COMPANY_PROFILE 테이블(기업정보)과 매핑되는 DTO
 */
@Getter
@Setter
public class CompanyProfileDTO {

    private Long id; // 기본키
    private Long memberId; // MEMBER 테이블의 회원 고유번호
    private String companyName; // 회사명
    private String businessNumber; // 사업자등록번호
    private String representativeName; // 대표자명
    private String industry; // 업종
    private String companyAddress; // 회사 주소
    private String companyDetailAddress; // 회사 상세 주소
    private String companyPhone; // 회사 전화번호
    private String websiteUrl; // 홈페이지
    private String logoUrl; // 로고 이미지
    private String companyDescription; // 회사 소개
    private Integer employeeCount; // 직원 수
    private String establishedDate; // 설립일
    private String verificationStatus; // 인증 상태
    private String createdAt; // 등록일
    private String updatedAt; // 수정일

    // MEMBER 테이블과 JOIN하여 가져오는 값
    private String loginId; // 로그인 아이디
    private String name; // 담당자 이름
    private String role; // 회원 구분

    private String existsYn; // 사업자등록번호 중복 여부(Y/N)

}
