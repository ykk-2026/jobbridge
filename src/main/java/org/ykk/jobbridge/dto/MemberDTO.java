package org.ykk.jobbridge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * MEMBER 테이블(회원정보)과 매핑되는 DTO
 * DB 컬럼명은 언더스코어 표기(login_id), DTO 변수는 카멜식(loginId)으로 정의함
 *
 * lombok은 코딩을 줄이기 위해 @어노테이션을 통한 자동 코드 완성기능임
 *
 * @Getter => getter 함수를 작성하지 않았지만, 자동 생성
 * @Setter => setter 함수를 작성하지 않았지만, 자동 생성
 * @JsonInclude(JsonInclude.Include.NON_DEFAULT) => 값이 존재하는 변수만 JSON 생성
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class MemberDTO {

    private Long id; // 기본키, 회원 고유번호
    private String loginId; // 로그인 아이디
    private String password; // 비밀번호(SHA-256 해시 암호화 저장)
    private String name; // 이름
    private String birthDate; // 생년월일
    private String gender; // 성별
    private String email; // 이메일
    private String phone; // 전화번호
    private String role; // 회원 구분(JOB_SEEKER, COMPANY, ADMIN)
    private String status; // 계정 상태(ACTIVE 등)

    private String desiredJob; // 회원가입 시 JOB_SEEKER_PROFILE 테이블에 저장할 희망 직무

    private String existsYn; // 아이디, 이메일 중복 여부(Y/N)

}
